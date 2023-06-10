package com.denchic45.studiversity.data.db.remote.source

import com.denchic45.studiversity.data.db.remote.model.GroupMap
import com.denchic45.studiversity.util.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

actual class GroupRemoteDataSource @Inject constructor(
    val firestore: FirebaseFirestore,
) {
    private val groupsRef: CollectionReference = firestore.collection("Groups")
    private val coursesRef: CollectionReference = firestore.collection("Courses")
    private val usersRef: CollectionReference = firestore.collection("Users")

    actual fun findByContainsName(text: String): Flow<List<GroupMap>> {
        return groupsRef
            .whereArrayContains("searchKeys", SearchKeysGenerator.formatInput(text))
            .getQuerySnapshotFlow()
            .map {
                it.toMaps(::GroupMap)
            }
    }

    actual suspend fun findById(id: String): GroupMap {
        return GroupMap(groupDocReference(id).get().await().toMap())
    }

    actual fun observeById(id: String): Flow<GroupMap?> {
        return groupDocReference(id)
            .getDocumentSnapshotFlow()
            .map {
                if (it.data != null)
                    it.toMap(::GroupMap)
                else null
            }
    }

    actual suspend fun findCoursesByGroupId(groupId: String): List<String> {
        return coursesRef.whereArrayContains("groupIds", groupId)
            .get()
            .await()
            .map { snapshot -> snapshot.id }
    }

    private fun groupDocReference(groupId: String) = groupsRef.document(groupId)

    actual suspend fun removeGroupAndRemoveStudentsAndRemoveGroupIdInCourses(
        groupId: String,
        studentIds: Set<String>,
        groupCourseIds: List<String>,
    ) {
        val batch = firestore.batch().delete(groupDocReference(groupId))
        studentIds.forEach { userId -> batch.delete(usersRef.document(userId)) }
        groupCourseIds.forEach { courseId ->
            batch.update(
                coursesRef.document(courseId),
                "groupIds",
                FieldValue.arrayRemove(groupId)
            )
        }
        batch.commit().await()
    }

    actual suspend fun add(groupMap: MutableFireMap) {
        groupMap["timestamp"] = FieldValue.serverTimestamp()

        groupDocReference(groupMap["id"] as String)
            .set(groupMap, SetOptions.merge())
            .await()
    }

    actual suspend fun update(groupMap: MutableFireMap) {
        groupMap["timestamp"] = FieldValue.serverTimestamp()

        groupDocReference(groupMap["id"] as String).update(groupMap).await()
    }

    actual suspend fun updateGroupCurator(groupId: String, teacherMap: MutableFireMap) {
        val updatedGroupMap: MutableFireMap = mutableMapOf(
            "curator" to teacherMap,
            "timestamp" to FieldValue.serverTimestamp()
        )
        groupDocReference(groupId).update(updatedGroupMap).await()
    }

    actual suspend fun findBySpecialtyId(specialtyId: String): List<GroupMap> {
        return groupsRef.whereEqualTo("specialty.id", specialtyId).get()
            .await().toMaps(::GroupMap)
    }

    actual fun findByTeacherIdAndTimestamp(
        teacherId: String,
        timestampGroups: Long,
    ): Flow<List<GroupMap>> = groupsRef.whereArrayContains("teacherIds", teacherId)
        .whereGreaterThan("timestamp", Date(timestampGroups))
        .getQuerySnapshotFlow()
        .map { it.toMaps(::GroupMap) }

    actual suspend fun findByCuratorId(id: String): GroupMap {
        return groupsRef.whereEqualTo("curator.id", id)
            .get().await().documents[0].toMap(::GroupMap)
    }

    actual fun observeByCuratorId(id: String): Flow<GroupMap?> {
        return groupsRef.whereEqualTo("curator.id", id)
            .getQuerySnapshotFlow()
            .map {
                if (it.isEmpty)
                    null
                else
                    it.documents[0].toMap(::GroupMap)
            }
    }

    actual suspend fun findByCourse(course: Int): List<GroupMap> = groupsRef
        .whereEqualTo("course", course)
        .get()
        .await()
        .toMaps(::GroupMap)

    actual suspend fun setHeadman(studentId: String, groupId: String) {
        groupsRef.document(groupId).update("headmanId", studentId).await()
    }

    actual suspend fun removeHeadman(groupId: String) {
        groupsRef.document(groupId).update("headmanId", null).await()
    }

    actual suspend fun findByIdIn(groupIds: List<String>): List<GroupMap>? {
        return groupsRef.whereIn("id", groupIds)
            .get()
            .await()
            .run {
                if (timestampsNotNull()) {
                    toMaps(::GroupMap)
                } else {
                    null
                }
            }
    }


    actual suspend fun updateGroupsOfCourse(groupIds: List<String>) {
        groupIds.forEach { groupId ->
            groupsRef.document(groupId).update(
                "timestamp",
                FieldValue.serverTimestamp()
            ).await()

            groupsRef.document(groupId).update(
                "timestampCourses",
                FieldValue.serverTimestamp()
            ).await()
        }
    }

}