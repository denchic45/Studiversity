package com.denchic45.kts.data.db.local.source

import com.denchic45.kts.*
import com.denchic45.kts.data.db.local.model.CourseWithSubjectAndTeacherEntities
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class CourseLocalDataSource @Inject constructor(private val db: AppDatabase) {

    private val queries: CourseEntityQueries = db.courseEntityQueries

    fun upsert(courseEntity: CourseEntity) {
//        withContext(Dispatchers.IO) {
        queries.upsert(courseEntity)
//        }
    }

    fun observeById(id: String): Flow<List<CourseWithSubjectEntity>> {
        return queries.getById(id).asFlow()
            .mapToList(Dispatchers.IO)
    }

    suspend fun get(id: String): List<GetCourseWithSubjectWithTeacherAndGroupsById> =
        withContext(Dispatchers.IO) {
            queries.getCourseWithSubjectWithTeacherAndGroupsById(id).executeAsList()
        }

    fun observeCoursesByStudyGroupId(groupId: String): Flow<List<CourseWithSubjectEntity>> {
        return queries.getCoursesByStudyGroupId(groupId)
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    private suspend fun hasRelatedTeacherToGroup(teacherId: String, groupId: String): Boolean =
        withContext(Dispatchers.IO) {
            queries.hasRelatedTeacherToGroup(groupId, teacherId).executeAsOne()
        }

    suspend fun hasRelatedSubjectToGroup(subjectId: String, groupId: String): Boolean =
        withContext(Dispatchers.IO) {
            queries.hasRelatedSubjectToGroup(groupId, subjectId).executeAsOne()
        }

    suspend fun getNotRelatedTeacherIdsToGroup(
        teacherIds: List<String>,
        groupId: String,
    ): List<String> {
        return teacherIds
            .filter { teacherId: String -> !hasRelatedTeacherToGroup(teacherId, groupId) }

    }

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

    suspend fun isCourseTeacher(courseId: String, teacherId: String): Boolean =
        withContext(Dispatchers.IO) {
            queries.isCourseTeacher(courseId, teacherId).executeAsOne()
        }

    suspend fun getCourseIdByContentId(taskId: String): String = withContext(Dispatchers.IO) {
        queries.getCourseIdByContentId(taskId).executeAsOne()
    }

    fun saveCourse(
        subjectEntity: SubjectEntity?,
        courseEntity: CourseEntity
    ) {
        db.transaction {
//            db.groupCourseEntityQueries.apply {
//                deleteByCourseId(courseId)
//                groupCourseEntities.forEach { upsert(it) }
//            }
//            db.userEntityQueries.upsert(teacherEntity)
            subjectEntity?.let { db.subjectEntityQueries.upsert(it) }
            queries.upsert(courseEntity)
//            db.sectionEntityQueries.apply {
//                deleteByCourseId(courseId)
//                sectionEntities.forEach { upsert(it) }
//            }
        }
    }
}