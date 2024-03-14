package com.denchic45.studiversity.feature.course.work.submission

import com.denchic45.studiversity.database.exists
import com.denchic45.studiversity.database.table.*
import com.denchic45.stuiversity.api.course.work.grade.GradeResponse
import com.denchic45.stuiversity.api.course.work.grade.SubmissionGradeRequest
import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
import com.denchic45.stuiversity.api.course.work.submission.model.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import java.time.LocalDateTime
import java.util.*

class SubmissionRepository {

    fun addNewSubmissionByStudentId(courseWorkId: UUID, studentId: UUID): SubmissionResponse {
        return addSubmissionByStudentId(courseWorkId, studentId, SubmissionState.NEW)
    }

    fun addCreatedSubmissionByStudentId(courseWorkId: UUID, studentId: UUID): SubmissionResponse {
        return addSubmissionByStudentId(courseWorkId, studentId, SubmissionState.CREATED)
    }

    private fun addSubmissionByStudentId(
        courseWorkId: UUID,
        studentId: UUID,
        state: SubmissionState
    ): SubmissionResponse {
        return SubmissionDao.new {
            this.author = UserDao.findById(studentId)!!
            this.courseWork = CourseWorkDao.findById(courseWorkId)!!
            this.state = state
        }.toResponse()
    }

    fun addEmptySubmissionsByStudentIds(courseWorkId: UUID, studentIds: List<UUID>) {
        Submissions.batchInsert(studentIds) {
            set(Submissions.authorId, it)
            set(Submissions.courseWorkId, courseWorkId)
            set(Submissions.state, SubmissionState.NEW)
        }
    }

    fun find(submissionId: UUID): SubmissionResponse? {
        return SubmissionDao.findById(submissionId)?.toResponse()
    }

    fun findByStudentId(courseWorkId: UUID, studentId: UUID): SubmissionResponse? {
        return SubmissionDao.find(
            Submissions.courseWorkId eq courseWorkId and (Submissions.authorId eq studentId)
        ).singleOrNull()?.toResponse()
    }

    fun findByWorkId(
        courseId: UUID,
        courseWorkId: UUID,
        studentIds: List<UUID>
    ): List<SubmissionResponse> {
        return CourseWorks.leftJoin(
            Submissions,
            { CourseWorks.id },
            { Submissions.courseWorkId })
            .leftJoin(Grades, { Submissions.id }, { submissionId })
            .innerJoin(Users, { Submissions.authorId }, { Users.id })
            .select(
                CourseWorks.id eq courseWorkId
                        and (Submissions.authorId inList studentIds)
            )
            .map { row ->
                row.getOrNull(Submissions.id)?.let { submissionId ->
                    WorkSubmissionResponse(
                        id = submissionId.value,
                        author = Author(
                            id = row[Users.id].value,
                            firstName = row[Users.firstName],
                            surname = row[Users.surname],
                            avatarUrl = row[Users.avatarUrl]
                        ),
                        state = row[Submissions.state],
                        courseWorkId = courseWorkId,
                        content = when (CourseWorkDao.findById(courseWorkId)!!.type) {
                            CourseWorkType.ASSIGNMENT -> {
                                row[Submissions.content]?.let(Json.Default::decodeFromString)
                            }
                        } ?: WorkSubmissionContent(emptyList()),
                        updatedAt = row[Submissions.updatedAt],
                        grade = row.getOrNull(Grades.value)?.let {
                            GradeResponse(
                                value = row[Grades.value],
                                courseId = row[Grades.courseId].value,
                                studentId = row[Grades.studentId].value,
                                gradedBy = row[Grades.gradedBy]?.value,
                                submissionId = row[Grades.submissionId]?.value
                            )
                        }
                    )
                } ?: addNewSubmissionByStudentId(courseWorkId, row[Submissions.authorId].value)
            }
    }

    fun updateSubmissionState(submissionId: UUID, state: SubmissionState) {
        SubmissionDao.findById(submissionId)!!.state = state
    }

    fun updateSubmissionContent(
        submissionId: UUID,
        content: SubmissionContent?
    ): SubmissionResponse? {
        return SubmissionDao.findById(submissionId)?.apply {
            this.content = Json.encodeToString(content)
        }?.toResponse()
    }

    fun submitSubmission(submissionId: UUID): SubmissionResponse {
        return SubmissionDao.findById(submissionId)!!.apply {
            state = SubmissionState.SUBMITTED
            updateAt = LocalDateTime.now()
        }.toResponse()
    }

    fun cancelSubmission(submissionId: UUID): SubmissionResponse {
        return SubmissionDao.findById(submissionId)!!.apply {
            state = SubmissionState.CANCELED_BY_AUTHOR
            updateAt = LocalDateTime.now()
        }.toResponse()
    }

    fun isExists(submissionId: UUID): Boolean {
        return Submissions.exists { Submissions.id eq submissionId }
    }

    fun isAuthorBySubmissionId(submissionId: UUID, authorId: UUID): Boolean {
        return Submissions.exists { Submissions.id eq submissionId and (Submissions.authorId eq authorId) }
    }

    fun setGradeSubmission(grade: SubmissionGradeRequest): SubmissionResponse {
        GradeDao.new {
            this.course = CourseDao.findById(grade.courseId)!!
            this.student = SubmissionDao.findById(grade.submissionId)!!.author
            this.gradedBy = UserDao.findById(grade.gradedBy)
            this.value = grade.value
            this.submission = SubmissionDao.findById(grade.submissionId)
        }
        return SubmissionDao.findById(grade.submissionId)!!.toResponse()
    }

    fun removeGradeSubmission(submissionId: UUID): Boolean {
        return GradeDao.find(Grades.submissionId eq submissionId).singleOrNull()?.delete() != null
    }
}