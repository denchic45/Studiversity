package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

object Grades : LongIdTable("grade", "grade_id") {
    val courseId = reference("course_id", Courses, onDelete = ReferenceOption.CASCADE)
    val studentId = reference("student_id", Users, onDelete = ReferenceOption.CASCADE)
    val gradedBy = reference("graded_by", Users, onDelete = ReferenceOption.SET_NULL).nullable()
    val value = integer("value")
    val submissionId: Column<EntityID<UUID>?> =
        reference("submission_id", Submissions, onDelete = ReferenceOption.SET_NULL).nullable()
}

class GradeDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<GradeDao>(Grades)

    var course by CourseDao referencedOn Grades.courseId
    var student by UserDao referencedOn Grades.studentId
    var gradedBy by UserDao optionalReferencedOn Grades.gradedBy
    var value by Grades.value

    var submission by SubmissionDao optionalReferencedOn Grades.submissionId
}