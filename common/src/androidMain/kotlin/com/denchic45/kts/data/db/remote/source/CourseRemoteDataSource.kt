package com.denchic45.kts.data.db.remote.source

import com.denchic45.kts.data.db.remote.model.CourseContentMap
import com.denchic45.kts.data.db.remote.model.CourseMap
import com.denchic45.kts.data.db.remote.model.SectionMap
import com.denchic45.kts.data.db.remote.model.SubmissionMap
import com.denchic45.kts.data.domain.model.TaskStatus
import com.denchic45.kts.util.*
import com.google.firebase.firestore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

actual class CourseRemoteDataSource @Inject constructor (
    private val firestore: FirebaseFirestore,
    private val groupRemoteDataSource: GroupRemoteDataSource,
) {
    private val groupsRef: CollectionReference = firestore.collection("Groups")
    private val coursesRef: CollectionReference = firestore.collection("Courses")
    private val contentsRef: Query = firestore.collectionGroup("Contents")

    actual suspend fun removeCourse(courseId: String, groupIds: List<String>) {
        val batch = firestore.batch()
        batch.delete(coursesRef.document(courseId))
        groupIds.forEach { groupId ->
            batch.update(
                groupsRef.document(groupId),
                "timestamp",
                FieldValue.serverTimestamp()
            )

            batch.update(
                groupsRef.document(groupId),
                "timestampCourses",
                FieldValue.serverTimestamp()
            )
        }
        coursesRef.document(courseId).collection("Contents").deleteCollection(10)
        batch.commit().await()
    }

    actual suspend fun findByGroupIds(groupIds: List<String>): List<CourseMap> = coursesRef
        .whereArrayContainsAny("groupIds", groupIds)
        .get()
        .await()
        .toMaps(::CourseMap)

    actual suspend fun addCourse(courseMap: CourseMap) {
        val batch = firestore.batch()
        (courseMap.groupIds).forEach { groupId ->
            batch.update(
                groupsRef.document(groupId),
                "timestamp",
                FieldValue.serverTimestamp()
            )

            batch.update(
                groupsRef.document(groupId),
                "timestampCourses",
                FieldValue.serverTimestamp()
            )
        }
        batch.set(coursesRef.document(courseMap.id), courseMap.map)
        batch.commit().await()
    }

    actual suspend fun removeGroupFromCourses(groupId: String) {
        val courseDocs = coursesByGroupIdQuery(groupId)
            .get()
            .await()
            .toMaps(::CourseMap)
        val batch = firestore.batch()
        courseDocs.forEach { courseDoc ->
            batch.update(
                coursesRef.document(courseDoc.id),
                mapOf(
                    timestampFiledPair(),
                    "groupIds" to FieldValue.arrayRemove(groupId)
                )
            )
        }
        batch.commit().await()
    }

    private fun timestampFiledPair() = "timestamp" to FieldValue.serverTimestamp()

    private fun coursesByGroupIdQuery(groupId: String): Query {
        return coursesRef.whereArrayContains("groupIds", groupId)
    }

    actual suspend fun findByGroupId(groupId: String): List<CourseMap> {
        return coursesByGroupIdQuery(groupId).get().await().toMaps(::CourseMap)
    }

    actual fun observeById(courseId: String): Flow<CourseMap?> {
        return coursesRef.document(courseId).getDocumentSnapshotFlow().map {
            if (it.exists() && it.timestampNotNull())
                it.toMap(::CourseMap)
            else null
        }
    }

    actual suspend fun updateContentOrder(courseId: String, contentId: String, order: Int) {
        getContentDocument(courseId, contentId)
            .update(
                mapOf(
                    "order" to order,
                    timestampFiledPair()
                )
            )
            .await()
    }

    private fun getContentDocument(courseId: String, contentId: String): DocumentReference =
        coursesRef.document(courseId)
            .collection("Contents")
            .document(contentId)

    actual suspend fun removeCourseContent(courseId: String, contentId: String) {
        getContentDocument(courseId, contentId)
            .set(
                mapOf(
                    "id" to contentId,
                    "timestamp" to Date(),
                    "deleted" to true
                )
            )
            .await()
    }

    actual suspend fun gradeSubmission(
        courseId: String,
        taskId: String,
        studentId: String,
        grade: Int,
        teacherId: String,
    ) {
        getContentDocument(courseId, taskId).update(
            mapOfSubmissionFields(courseId, taskId, studentId)
                    +
                    mapOf(
                        "submissions.$studentId.status" to TaskStatus.GRADED,
                        "submissions.$studentId.gradedDate" to Date(),
                        "submissions.$studentId.grade" to grade,
                        "submissions.$studentId.teacherId" to teacherId,

                        "submittedByStudentIds" to FieldValue.arrayUnion(studentId),
                        "notSubmittedByStudentIds" to FieldValue.arrayRemove(studentId),
                    )
        ).await()
    }

    private fun mapOfSubmissionFields(
        courseId: String,
        taskId: String,
        studentId: String,
    ) = mapOf(
        "timestamp" to FieldValue.serverTimestamp(),
        "submissions.$studentId.studentId" to studentId,
        "submissions.$studentId.contentId" to taskId,
        "submissions.$studentId.courseId" to courseId,
    )

    actual suspend fun rejectSubmission(
        courseId: String,
        taskId: String,
        studentId: String,
        cause: String,
        teacherId: String,
    ) {
        getContentDocument(courseId, taskId).update(
            mapOfSubmissionFields(courseId, taskId, studentId)
                    +
                    mapOf(
                        "timestamp" to FieldValue.serverTimestamp(),
                        "submissions.$studentId.status" to TaskStatus.REJECTED,
                        "submissions.$studentId.cause" to cause,
                        "submissions.$studentId.rejectedDate" to Date(),
                        "submissions.$studentId.teacherId" to teacherId,

                        "submittedByStudentIds" to FieldValue.arrayRemove(studentId),
                        "notSubmittedByStudentIds" to FieldValue.arrayUnion(studentId),
                    )
        ).await()
    }

    actual suspend fun updateSubmissionFromStudent(
        submissionMap: SubmissionMap,
        studentId: String,
        attachmentUrls: List<String>,
        courseId: String,
        contentId: String,
    ) {
//        val submittedDate = submissionMap.submittedDate

        val updatedFields = mutableMapOf<String, Any?>(
//            "submissions.$studentId.text" to submissionMap.text,
//            "submissions.$studentId.attachments" to attachmentUrls,
            "submissions.$studentId.status" to submissionMap.status, //TODO проверить корректность статуса!
            "submissions.$studentId.timestamp" to FieldValue.serverTimestamp(),
            "timestamp" to FieldValue.serverTimestamp(),
            "submissions.$studentId.submittedDate" to FieldValue.serverTimestamp(),
            "submittedByStudentIds" to
                    if (submissionMap.submitted)
                        FieldValue.arrayUnion(studentId)
                    else
                        FieldValue.arrayRemove(studentId),
            "notSubmittedByStudentIds" to
                    if (submissionMap.submitted)
                        FieldValue.arrayRemove(studentId)
                    else
                        FieldValue.arrayUnion(studentId)
        ).apply {
            submissionMap.map.forEach { (key, value) -> put("submissions.$studentId.$key", value) }
        }
        coursesRef.document(courseId)
            .collection("Contents")
            .document(contentId)
            .update(updatedFields)
            .await()
    }

    actual fun findContentByCourseId(
        courseId: String,
        timestampContentsOfCourse: Long,
    ): Flow<List<CourseContentMap>?> {
        return coursesRef.document(courseId).collection("Contents").run {
            if (timestampContentsOfCourse == 0L)
                whereEqualTo("deleted", false)
            else
                whereGreaterThan("timestamp", Date(timestampContentsOfCourse))
        }.getDataFlow {
            if (it.timestampsNotNull())
                it.toMutableMaps(::CourseContentMap)
            else null
        }
    }

    actual fun findCoursesByTeacherId(
        teacherId: String,
        timestampPreferences: Long,
    ): Flow<List<CourseMap>> {
        return coursesRef.whereEqualTo("teacher.id", teacherId)
            .whereGreaterThan(
                "timestamp",
                Date(timestampPreferences)
            ).getDataFlow { it.toMaps(::CourseMap) }
    }

    actual suspend fun addTask(task: CourseContentMap) {
        coursesRef.document(task.courseId)
            .collection("Contents")
            .document(task.id)
            .set(task.map)
            .await()
    }

    actual suspend fun updateCourse(oldCourseMap: CourseMap, courseMap: CourseMap) {
        groupRemoteDataSource.updateGroupsOfCourse((oldCourseMap.groupIds + courseMap.groupIds))
        coursesRef.document(courseMap.id).update(
            FieldsComparator.mapOfDifference(oldCourseMap, courseMap)
        ).await()
    }

    actual fun findByContainsName(text: String): Flow<List<CourseMap>> {
        return coursesRef
            .whereArrayContains("searchKeys", text.lowercase())
            .getDataFlow { it.toMaps(::CourseMap) }
    }

    actual suspend fun findLastContentOrderByCourseIdAndSectionId(
        courseId: String,
        sectionId: String,
    ): Long {
        val snapshot = coursesRef.document(courseId).collection("Contents")
            .whereEqualTo("sectionId", sectionId)
            .orderBy("order", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()

        return if (snapshot.isEmpty) 0L
        else snapshot.documents[0].getLong("order")!!
    }

    actual suspend fun updateTask(courseId: String, id: String, updatedFields: MutableFireMap) {
        coursesRef.document(courseId)
            .collection("Contents")
            .document(id)
            .update(updatedFields.apply { put("timestamp", FieldValue.serverTimestamp()) })
            .await()
    }

    actual suspend fun updateCourseSections(sections: List<FireMap>) {
        coursesRef.document(sections[0]["courseId"] as String)
            .update("sections", sections)
            .await()
    }

    actual suspend fun addSection(map: FireMap) {
        coursesRef.document(map["courseId"] as String)
            .update("sections", FieldValue.arrayUnion(map))
            .await()
    }

    actual suspend fun removeSection(sectionMap: SectionMap) {
        val courseDocRef = coursesRef.document(sectionMap.courseId)
        val contentsRef = courseDocRef.collection("Contents")

        val contentsWithThisSection = contentsRef
            .whereEqualTo("sectionId", sectionMap.id)
            .get()
            .await()

        val batch = firestore.batch()

        contentsWithThisSection.forEach {
            batch.update(
                contentsRef
                    .document(it.getString("id")!!), "sectionId", ""
            )
        }

        batch.update(courseDocRef, "sections", FieldValue.arrayRemove(sectionMap.map))
        batch.commit().await()
    }

    actual fun findUpcomingTasksByGroupId(userId: String): Flow<List<CourseContentMap>> {
        return contentsRef.whereGreaterThanOrEqualTo(
            "completionDate",
            Date()
        )
            .whereArrayContains(
                "notSubmittedByStudentIds",
                userId
            )
            .limit(10)
            .getDataFlow { it.toMutableMaps(::CourseContentMap) }

    }

    actual suspend fun findOverdueTasksByGroupId(userId: String): List<CourseContentMap> {
        return contentsRef.whereLessThanOrEqualTo(
            "completionDate",
            Date()
        )
            .whereArrayContains(
                "notSubmittedByStudentIds",
                userId
            )
            .limit(10)
            .get()
            .await()
            .map { it.toMutableMap(::CourseContentMap) }
    }

    actual suspend fun findCompletedTasksByStudentId(userId: String): List<CourseContentMap> {
        return contentsRef
            .whereArrayContains(
                "submittedByStudentIds",
                userId
            )
            .limit(10)
            .get()
            .await()
            .toMutableMaps(::CourseContentMap)
    }
}