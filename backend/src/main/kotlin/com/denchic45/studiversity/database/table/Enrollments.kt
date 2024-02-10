package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Table

object Enrollments : LongIdTable("enrollment", "enrollment_id") {
    val courseId = reference("course_id", Courses.id)
    val type = enumerationByName<EnrollmentType>("type", 16)
}

class EnrollmentDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<EnrollmentDao>(Enrollments)

    var course by CourseDao referencedOn Enrollments.courseId
    var type by Enrollments.type
}

enum class EnrollmentType { DEFAULT, STUDY_GROUPS }