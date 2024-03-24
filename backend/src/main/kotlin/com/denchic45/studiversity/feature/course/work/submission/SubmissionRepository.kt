package com.denchic45.studiversity.feature.course.work.submission

import com.denchic45.studiversity.database.exists
import com.denchic45.studiversity.database.table.*
import com.denchic45.stuiversity.api.course.work.grade.SubmissionGradeRequest
import com.denchic45.stuiversity.api.submission.model.SubmissionByAuthor
import com.denchic45.stuiversity.api.submission.model.SubmissionContent
import com.denchic45.stuiversity.api.submission.model.SubmissionResponse
import com.denchic45.stuiversity.api.submission.model.SubmissionState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDateTime
import java.util.*

class SubmissionRepository {

//    fun getNewSubmissionByStudentId(courseWorkId: UUID, studentId: UUID): SubmissionResponse {
////        return addSubmissionByStudentId(courseWorkId, studentId, SubmissionState.CREATED)
//        return when (CourseWorkDao[courseWorkId].type) {
//            CourseWorkType.ASSIGNMENT -> WorkSubmissionResponse()
//        }
//    }

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

//    fun addEmptySubmissionsByStudentIds(courseWorkId: UUID, studentIds: List<UUID>) {
//        Submissions.batchInsert(studentIds) {
//            set(Submissions.authorId, it)
//            set(Submissions.courseWorkId, courseWorkId)
//            set(Submissions.state, SubmissionState.NEW)
//        }
//    }

    fun find(submissionId: UUID): SubmissionResponse? {
        return SubmissionDao.findById(submissionId)?.toResponse()
    }

    fun findByStudentId(courseWorkId: UUID, studentId: UUID): SubmissionResponse {
        return SubmissionDao.find(
            Submissions.courseWorkId eq courseWorkId and (Submissions.authorId eq studentId)
        ).singleOrNull()?.toResponse()
            ?: addCreatedSubmissionByStudentId(courseWorkId, studentId)
    }

    fun findByWorkId(
        courseWorkId: UUID,
        studentIds: List<UUID>
    ): List<SubmissionByAuthor> = CourseWorks.leftJoin(
        Submissions,
        { CourseWorks.id },
        { Submissions.courseWorkId })
        .leftJoin(Grades, { Submissions.id }, { submissionId })
        .innerJoin(Users, { Submissions.authorId }, { Users.id })
        .selectAll()
        .where(CourseWorks.id eq courseWorkId and (Submissions.authorId inList studentIds))
        .map { row ->
            SubmissionByAuthor(
                UserDao.wrapRow(row).toAuthor(), row.getOrNull(Submissions.id)?.let {
                    SubmissionDao.wrapRow(row).toResponse()
                }
            )
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
            this.course = CourseDao.findById(findCourseIdBySubmissionId(grade.submissionId))!!
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

    fun findCourseIdBySubmissionId(submissionId: UUID): UUID {
        return CourseElementDao[SubmissionDao[submissionId].courseWork.id].course.id.value
    }
}