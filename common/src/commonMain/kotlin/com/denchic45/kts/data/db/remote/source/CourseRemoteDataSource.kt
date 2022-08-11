package com.denchic45.kts.data.db.remote.source

import com.denchic45.kts.data.db.remote.model.CourseContentMap
import com.denchic45.kts.data.db.remote.model.CourseMap
import com.denchic45.kts.data.db.remote.model.SectionMap
import com.denchic45.kts.data.db.remote.model.SubmissionMap
import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.MutableFireMap
import kotlinx.coroutines.flow.Flow

expect class CourseRemoteDataSource {
    suspend fun removeCourse(courseId: String, groupIds: List<String>)

    suspend fun findByGroupIds(groupIds: List<String>): List<CourseMap>

    suspend fun addCourse(courseMap: CourseMap)

    suspend fun removeGroupFromCourses(groupId: String)

    suspend fun findByGroupId(groupId: String): List<CourseMap>

    fun observeById(courseId: String): Flow<CourseMap?>

    suspend fun updateContentOrder(
        courseId: String,
        contentId: String,
        order: Int,
    )

    suspend fun removeCourseContent(courseId: String, contentId: String)

    suspend fun gradeSubmission(
        courseId: String,
        taskId: String,
        studentId: String,
        grade: Int,
        teacherId: String,
    )

    suspend fun rejectSubmission(
        courseId: String,
        taskId: String,
        studentId: String,
        cause: String,
        teacherId: String,
    )

    suspend fun updateSubmissionFromStudent(
        submissionMap: SubmissionMap,
        studentId: String,
        attachmentUrls: List<String>,
        courseId: String,
        contentId: String,
    )

    fun findContentByCourseId(
        courseId: String,
        timestampContentsOfCourse: Long,
    ): Flow<List<CourseContentMap>?>

    fun findCoursesByTeacherId(
        teacherId: String,
        timestampPreferences: Long,
    ): Flow<List<CourseMap>>

    suspend fun addTask(task: CourseContentMap)

    suspend fun updateCourse(oldCourseMap: CourseMap, courseMap: CourseMap)

    fun findByContainsName(text: String): Flow<List<CourseMap>>

    suspend fun findLastContentOrderByCourseIdAndSectionId(
        courseId: String,
        sectionId: String,
    ): Long

    suspend fun updateTask(courseId: String, id: String, updatedFields: MutableFireMap)

    suspend fun updateCourseSections(sections: List<FireMap>)

    suspend fun addSection(map: FireMap)

    suspend fun removeSection(sectionMap: SectionMap)

    suspend fun findOverdueTasksByGroupId(userId: String): List<CourseContentMap>

    suspend fun findCompletedTasksByStudentId(userId: String): List<CourseContentMap>

    fun findUpcomingTasksByGroupId(userId: String): Flow<List<CourseContentMap>>
}