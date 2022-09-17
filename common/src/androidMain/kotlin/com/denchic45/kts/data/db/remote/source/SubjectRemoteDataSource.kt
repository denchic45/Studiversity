package com.denchic45.kts.data.db.remote.source

import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.db.remote.model.CourseMap
import com.denchic45.kts.data.db.remote.model.SubjectMap
import com.denchic45.kts.data.remote.db.RemoveCourseOperation
import com.denchic45.kts.data.remote.db.UpdateGroupsOfCourse
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.util.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

actual class SubjectRemoteDataSource @Inject constructor(
    val firestore: FirebaseFirestore,
    override val networkService: NetworkService,
    override val appVersionService: AppVersionService,
    override val courseRemoteDataSource: CourseRemoteDataSource,
) : UpdateGroupsOfCourse, RemoveCourseOperation {
    private val subjectsRef: CollectionReference = firestore.collection("Subjects")
    override val groupsRef: CollectionReference = firestore.collection("Groups")
    private val coursesRef: CollectionReference = firestore.collection("Courses")

    actual suspend fun add(subjectMap: SubjectMap) {
        isExistWithSameIconAndColor(subjectMap)
        subjectsRef.document(subjectMap.id)
            .set(subjectMap.map)
            .await()
    }

    actual suspend fun isExistWithSameIconAndColor(subjectMap: SubjectMap) {
        val snapshot = subjectsRef
            .whereNotEqualTo("id", subjectMap.id)
            .whereEqualTo("iconUrl", subjectMap.iconUrl)
            .whereEqualTo("colorName", subjectMap.colorName)
            .get()
            .await()
        if (!snapshot.isEmpty) {
            throw SameSubjectIconException()
        }
    }

    actual suspend fun update(subjectMap: SubjectMap) {
        isExistWithSameIconAndColor(subjectMap)
        firestore.batch().apply {
            this[subjectsRef.document(subjectMap.id)] = subjectMap.map
            coursesRef.whereEqualTo("subject.id", subjectMap.id).get().await()
                .forEach { docSnapshot ->
                    @Suppress("UNCHECKED_CAST")
                    updateGroupsOfCourse(this, (docSnapshot.get("groupIds") as List<String>))
                    update(
                        coursesRef.document(docSnapshot.id),
                        "subject",
                        subjectMap.map,
                        "timestamp",
                        FieldValue.serverTimestamp()
                    )
                }
            commit().await()
        }
    }

    actual suspend fun remove(map: FireMap) {
        val subjectId = map["id"] as String
        subjectsRef.document(subjectId).delete().await()
        coursesRef.whereEqualTo("subject.id", subjectId)
            .get()
            .await()
            .forEach {
                val courseMap = it.toMap(::CourseMap)
                removeCourse(courseMap.id, courseMap.groupIds)
            }
    }

    actual fun observeById(id: String): Flow<SubjectMap?> {
        return subjectsRef.document(id)
            .getDocumentSnapshotFlow()
            .map { snapshot -> snapshot.toMap(::SubjectMap) }
    }

    actual suspend fun findById(id: String): SubjectMap {
        return subjectsRef.document(id)
            .get()
            .await()
            .toMap(::SubjectMap)
    }

    actual suspend fun findByGroupId(groupId: String): List<CourseMap> {
        return coursesRef.whereArrayContains("groupIds", groupId)
            .get()
            .await()
            .toMaps(::CourseMap)
    }

    actual fun findByContainsName(text: String): Flow<List<SubjectMap>> {
        return subjectsRef
            .whereArrayContains("searchKeys", text.lowercase(Locale.getDefault()))
            .getDataFlow { snapshot -> snapshot.toMaps(::SubjectMap) }
    }
}