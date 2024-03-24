package com.denchic45.studiversity.database.table


import com.denchic45.stuiversity.api.submission.model.SubmissionState
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

object Submissions : UUIDTable("submission", "submission_id") {
    val courseWorkId = reference("course_work_id", CourseWorks)
    val authorId = reference("author_id", Users)
    val content = text("content").nullable()
    val state = enumerationByName<SubmissionState>("state", 20)

    val updatedAt = datetime("updated_at").nullable()
}

class SubmissionDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SubmissionDao>(Submissions)

    var content by Submissions.content
    var state by Submissions.state
    var updateAt by Submissions.updatedAt

    var courseWork by CourseWorkDao referencedOn Submissions.courseWorkId
    var author by UserDao referencedOn Submissions.authorId
    val grade by GradeDao optionalBackReferencedOn Grades.submissionId
}