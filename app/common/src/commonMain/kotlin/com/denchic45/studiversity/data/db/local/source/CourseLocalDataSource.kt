package com.denchic45.studiversity.data.db.local.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.Course
import com.denchic45.studiversity.entity.CourseQueries
import com.denchic45.studiversity.entity.CourseWithSubject
import com.denchic45.studiversity.entity.GetCourseWithSubjectWithTeacherAndGroupsById
import com.denchic45.studiversity.entity.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class CourseLocalDataSource(private val db: AppDatabase) {

    private val queries: CourseQueries = db.courseQueries

    fun upsert(courseEntity: Course) {
//        withContext(Dispatchers.IO) {
        queries.upsert(courseEntity)
//        }
    }

    fun observeById(id: String): Flow<List<CourseWithSubject>> {
        return queries.getById(id).asFlow()
            .mapToList(Dispatchers.IO)
    }

    suspend fun get(id: String): List<GetCourseWithSubjectWithTeacherAndGroupsById> =
        withContext(Dispatchers.IO) {
            queries.getCourseWithSubjectWithTeacherAndGroupsById(id).executeAsList()
        }

//    fun observeCoursesByStudyGroupId(groupId: String): Flow<List<CourseWithSubject>> {
//        return queries.getCoursesByStudyGroupId(groupId)
//            .asFlow()
//            .mapToList(Dispatchers.IO)
//    }

//    private suspend fun hasRelatedTeacherToGroup(teacherId: String, groupId: String): Boolean =
//        withContext(Dispatchers.IO) {
//            queries.hasRelatedTeacherToGroup(groupId, teacherId).executeAsOne()
//        }

    suspend fun hasRelatedSubjectToGroup(subjectId: String, groupId: String): Boolean =
        withContext(Dispatchers.IO) {
            queries.hasRelatedSubjectToGroup(groupId, subjectId).executeAsOne()
        }

//    suspend fun getNotRelatedTeacherIdsToGroup(
//        teacherIds: List<String>,
//        groupId: String,
//    ): List<String> {
//        return teacherIds
//            .filter { teacherId: String -> !hasRelatedTeacherToGroup(teacherId, groupId) }
//    }

    suspend fun getNotRelatedSubjectIdsToGroup(
        subjectIds: List<String>,
        groupId: String,
    ): List<String> = subjectIds
        .filter { subjectId -> !hasRelatedSubjectToGroup(subjectId, groupId) }

//    fun getByTeacherId(id: String): Flow<List<GetCourseWithSubjectAndTeacherByTeacherId>> {
//        return queries.getCourseWithSubjectAndTeacherByTeacherId(id).asFlow()
//            .mapToList(Dispatchers.IO)
//    }

    suspend fun deleteById(courseId: String) = withContext(Dispatchers.IO) {
        queries.deleteById(courseId)
    }

//    suspend fun isCourseTeacher(courseId: String, teacherId: String): Boolean =
//        withContext(Dispatchers.IO) {
//            queries.isCourseTeacher(courseId, teacherId).executeAsOne()
//        }

    suspend fun getCourseIdByContentId(taskId: String): String = withContext(Dispatchers.IO) {
        queries.getCourseIdByContentId(taskId).executeAsOne()
    }

    fun saveCourse(
        subject: Subject?,
        courseEntity: Course
    ) {
        db.transaction {
//            db.groupCourseEntityQueries.apply {
//                deleteByCourseId(courseId)
//                groupCourseEntities.forEach { upsert(it) }
//            }
//            db.UserQueries.upsert(teacherEntity)
            subject?.let { db.subjectQueries.upsert(it) }
            queries.upsert(courseEntity)
//            db.CourseTopicQueries.apply {
//                deleteByCourseId(courseId)
//                sectionEntities.forEach { upsert(it) }
//            }
        }
    }
}