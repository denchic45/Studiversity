package com.denchic45.kts.data.db.remote.source

import com.denchic45.kts.data.db.remote.model.CourseContentMap
import com.denchic45.kts.data.db.remote.model.CourseMap
import com.denchic45.kts.data.db.remote.model.SectionMap
import com.denchic45.kts.data.db.remote.model.SubmissionMap
import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.MutableFireMap
import kotlinx.coroutines.flow.Flow

actual class CourseRemoteDataSource(groupRemoteDataSource: GroupRemoteDataSource) {
    actual suspend fun removeCourse(
        courseId: String,
        groupIds: List<String>,
    ) {
    }

    actual suspend fun findByGroupIds(groupIds: List<String>): List<CourseMap> {
        TODO("Not yet implemented")
    }

    actual suspend fun addCourse(courseMap: CourseMap) {
    }

    actual suspend fun removeGroupFromCourses(groupId: String) {
    }

    actual suspend fun findByGroupId(groupId: String): List<CourseMap> {
        TODO("Not yet implemented")
    }

    actual fun observeById(courseId: String): Flow<CourseMap?> {
        TODO("Not yet implemented")
    }

    actual suspend fun updateContentOrder(
        courseId: String,
        contentId: String,
        order: Int,
    ) {
    }

    actual suspend fun removeCourseContent(courseId: String, contentId: String) {
    }

    actual suspend fun gradeSubmission(
        courseId: String,
        taskId: String,
        studentId: String,
        grade: Int,
        teacherId: String,
    ) {
    }

    actual suspend fun rejectSubmission(
        courseId: String,
        taskId: String,
        studentId: String,
        cause: String,
        teacherId: String,
    ) {
    }

    actual suspend fun updateSubmissionFromStudent(
        submissionMap: SubmissionMap,
        studentId: String,
        attachmentUrls: List<String>,
        courseId: String,
        contentId: String,
    ) {
    }

    actual fun findContentByCourseId(
        courseId: String,
        timestampContentsOfCourse: Long,
    ): Flow<List<CourseContentMap>?> {
        TODO("Not yet implemented")
    }

    actual fun findCoursesByTeacherId(
        teacherId: String,
        timestampPreferences: Long,
    ): Flow<List<CourseMap>> {
        TODO("Not yet implemented")
    }

    actual suspend fun addTask(
        task: CourseContentMap,
    ) {
    }

    actual suspend fun updateCourse(
        oldCourseMap: CourseMap,
        courseMap: CourseMap,
    ) {
    }

    actual fun findByContainsName(text: String): Flow<List<CourseMap>> {
        TODO("Not yet implemented")
    }

    actual suspend fun findLastContentOrderByCourseIdAndSectionId(
        courseId: String,
        sectionId: String,
    ): Long {
        TODO("Not yet implemented")
    }

    actual suspend fun removeSection(sectionMap: SectionMap) {
    }

    actual suspend fun findOverdueTasksByGroupId(userId: String): List<CourseContentMap> {
        TODO("Not yet implemented")
    }

    actual suspend fun findCompletedTasksByStudentId(userId: String): List<CourseContentMap> {
        TODO("Not yet implemented")
    }

    actual fun findUpcomingTasksByGroupId(userId: String): Flow<List<CourseContentMap>> {
        TODO("Not yet implemented")
    }

    actual suspend fun updateTask(courseId: String, id: String, updatedFields: MutableFireMap) {
        TODO("Not yet implemented")
    }

    actual suspend fun updateCourseSections(sections: List<FireMap>) {
        TODO("Not yet implemented")
    }

    actual suspend fun addSection(map: FireMap) {
        TODO("Not yet implemented")
    }
}