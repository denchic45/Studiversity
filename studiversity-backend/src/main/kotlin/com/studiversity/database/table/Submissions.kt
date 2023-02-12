package com.studiversity.database.table

import com.studiversity.util.varcharMax
import com.stuiversity.api.course.work.submission.model.SubmissionState
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Submissions : UUIDTable("submission", "submission_id") {
    val courseWorkId = uuid("course_work_id").references(CourseWorks.id)
    val authorId = uuid("author_id").references(Users.id)
    val content = varcharMax("content").nullable()
    val state = enumerationByName<SubmissionState>("state", 20)
}

class SubmissionDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SubmissionDao>(Submissions)

    var courseWorkId by Submissions.courseWorkId
    var authorId by Submissions.authorId
    var content by Submissions.content
    var state by Submissions.state

    var courseWork by CourseWorkDao referencedOn Submissions.courseWorkId
    val grade by GradeDao optionalBackReferencedOn Grades.submissionId
}