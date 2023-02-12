package com.studiversity.database.table

import com.studiversity.util.varcharMax
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Courses : UUIDTable("course", "course_id") {
    val name = varcharMax("course_name")
    val subjectId = optReference("subject_id", Subjects.id)
    val archived = bool("archived").default(false)
}

class CourseDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CourseDao>(Courses)

    var name by Courses.name
    var subjectId by Courses.subjectId
    var archived by Courses.archived

    var subject by SubjectDao optionalReferencedOn Courses.subjectId
}