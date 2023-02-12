package com.denchic45.kts.data.db.local.source

import com.denchic45.kts.AppDatabase
import com.denchic45.kts.SubmissionEntity
import com.denchic45.kts.SubmissionEntityQueries
import com.denchic45.kts.UserEntity
import com.denchic45.kts.data.db.local.model.SubmissionWithStudentEntities
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class SubmissionLocalDataSource @Inject constructor(db: AppDatabase) {

    private val queries: SubmissionEntityQueries = db.submissionEntityQueries

    suspend fun upsert(submissionEntity: SubmissionEntity) = withContext(Dispatchers.IO) {
        queries.upsert(submissionEntity)
    }

    suspend fun upsert(submissionEntities: List<SubmissionEntity>) = withContext(Dispatchers.IO) {
        queries.transaction {
            submissionEntities.forEach { queries.upsert(it) }
        }
    }

    suspend fun deleteByContentId(id: String) = withContext(Dispatchers.IO) {
        queries.deleteByContentId(id)
    }

    fun getByTaskIdAndUserId(
        contentId: String,
        studentId: String,
    ): Flow<SubmissionWithStudentEntities?> {
        return queries.getSubmissionAndStudentByTaskIdAndUserId(
            contentId,
            studentId
        ) { submission_id, student_id, content_id, course_id, status, text, attachments, teacher_id, cause, grade, graded_date, rejectd_date, submitted_date, timestamp, user_id, first_name, surname, patronymic, user_group_id, role, email, photo_url, gender, admin, generated_avatar, timestamp_ ->
            SubmissionWithStudentEntities(
                SubmissionEntity(
                    submission_id,
                    student_id,
                    content_id,
                    course_id,
                    status,
                    text,
                    attachments,
                    teacher_id,
                    cause,
                    grade,
                    graded_date,
                    rejectd_date,
                    submitted_date,
                    timestamp
                ),
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
        }.asFlow().mapToOneOrNull(Dispatchers.IO)
    }

    fun getByTaskId(taskId: String): Flow<List<SubmissionWithStudentEntities>> {
        return queries.getSubmissionsAndStudentsByTaskId(taskId) { submission_id, student_id, content_id, course_id, status, text, attachments, teacher_id, cause, grade, graded_date, rejectd_date, submitted_date, timestamp, user_id, first_name, surname, patronymic, user_group_id, role, email, photo_url, gender, admin, generated_avatar, timestamp_ ->
            SubmissionWithStudentEntities(
                SubmissionEntity(
                    submission_id,
                    student_id,
                    content_id,
                    course_id,
                    status,
                    text,
                    attachments,
                    teacher_id,
                    cause,
                    grade,
                    graded_date,
                    rejectd_date,
                    submitted_date,
                    timestamp
                ),
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
            .asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun getStudentsWithoutSubmission(taskId: String): List<UserEntity> =
        withContext(Dispatchers.IO) {
            queries.getStudentsWithoutSubmission(taskId).executeAsList()
        }
}