package com.denchic45.studiversity.database.table

import com.denchic45.studiversity.database.type.timestampWithTimeZone
import com.denchic45.studiversity.util.varcharMax
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import java.util.*

object Courses : UUIDTable("course", "course_id") {
    val name = varcharMax("course_name")
    val subjectId = optReference("subject_id", Subjects)
    val archived = bool("archived").default(false)
    val createdAt = timestampWithTimeZone("created_at").defaultExpression(CurrentTimestamp())
    val updatedAt = timestampWithTimeZone("updated_at").nullable()
}

class CourseDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CourseDao>(Courses)

    var name by Courses.name
    var subjectId by Courses.subjectId
    var archived by Courses.archived
    var createdAt by Courses.createdAt
    var updatedAt by Courses.updatedAt

    var subject by SubjectDao optionalReferencedOn Courses.subjectId
}