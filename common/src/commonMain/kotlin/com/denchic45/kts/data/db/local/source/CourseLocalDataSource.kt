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

    fun observe(id: String): Flow<List<GetCourseWithSubjectWithTeacherAndGroupsById>> {
        return queries.getCourseWithSubjectWithTeacherAndGroupsById(id).asFlow()
            .mapToList(Dispatchers.IO)
    }

    suspend fun get(id: String): List<GetCourseWithSubjectWithTeacherAndGroupsById> =
        withContext(Dispatchers.IO) {
            queries.getCourseWithSubjectWithTeacherAndGroupsById(id).executeAsList()
        }

    fun observeCoursesByGroupId(groupId: String): Flow<List<CourseWithSubjectAndTeacherEntities>> {
        return queries.getCoursesWithSubjectAndTeacherByGroupId(groupId) { course_id, name, subject_id, teacher_id, user_id, first_name, surname, patronymic, user_group_id, role, email, photo_url, gender, admin, generated_avatar, timestamp, subject_id_, name_, icon_url, color_name ->
            CourseWithSubjectAndTeacherEntities(
                CourseEntity(course_id, name, subject_id, teacher_id),
                SubjectEntity(subject_id_, name_, icon_url, color_name),
                UserEntity(
                    user_id,
                    first_name,
                    surname,
                    patronymic,
                    user_group_id,
                    role,
                    email,
                    photo_url,
                    gender,
                    admin,
                    generated_avatar,
                    timestamp
                )
            )
        }
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

    fun getByTeacherId(id: String): Flow<List<GetCourseWithSubjectAndTeacherByTeacherId>> {
        return queries.getCourseWithSubjectAndTeacherByTeacherId(id).asFlow()
            .mapToList(Dispatchers.IO)
    }

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
        courseId: String,
        teacherEntity: UserEntity,
        subjectEntity: SubjectEntity,
        courseEntity: CourseEntity,
        sectionEntities: List<SectionEntity>,
        groupCourseEntities: List<GroupCourseEntity>,
    ) {
        db.transaction {
            db.groupCourseEntityQueries.apply {
                deleteByCourseId(courseId)
                groupCourseEntities.forEach { upsert(it) }
            }
            db.userEntityQueries.upsert(teacherEntity)
            db.subjectEntityQueries.upsert(subjectEntity)
            queries.upsert(courseEntity)
            db.sectionEntityQueries.apply {
                deleteByCourseId(courseId)
                sectionEntities.forEach { upsert(it) }
            }
        }
    }
}