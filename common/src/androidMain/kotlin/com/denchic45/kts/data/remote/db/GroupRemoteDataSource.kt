package com.denchic45.kts.data.remote.db

import com.denchic45.kts.data.remote.model.GroupMap
import com.denchic45.kts.util.SearchKeysGenerator
import com.denchic45.kts.util.getQuerySnapshotFlow
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

actual class GroupRemoteDataSource(
    val firestore: FirebaseFirestore
) {

    private val groupsRef: CollectionReference = firestore.collection("Groups")
    private val coursesRef: CollectionReference = firestore.collection("Courses")
    private val usersRef: CollectionReference = firestore.collection("Users")

    actual fun observeById(id: String): Flow<GroupMap> {
        TODO("Not yet implemented")
    }

    actual fun findByContainsName(text: String): Flow<List<GroupMap>> {
        return groupsRef
            .whereArrayContains("searchKeys", SearchKeysGenerator.formatInput(text))
            .getQuerySnapshotFlow()
            .map { snapshot -> snapshot.documents.map { GroupMap(it.data!!) } }
    }

    actual suspend fun remove(id: String) {
    }

    actual suspend fun findById(groupId: String): GroupMap {
        return GroupMap(
            groupDocReference(groupId)
                .get()
                .await()
                .data!!
        )
    }

    actual suspend fun findCoursesByGroupId(groupId: String): List<String> {
        return coursesRef.whereArrayContains("groupIds", groupId)
            .get()
            .await()
            .map { courseDocSnapshot -> courseDocSnapshot.id }
    }


    private fun groupDocReference(groupId: String) = groupsRef.document(groupId)

    suspend fun removeGroupAndRemoveStudentsAndRemoveGroupIdInCourses(
        groupId: String,
        studentIds: Set<String>,
        groupCourseIds: List<String>
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

}