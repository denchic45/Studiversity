package com.studiversity.database.table

import com.studiversity.database.expression.NowTimestamp
import com.studiversity.database.table.CourseElements.defaultExpression
import com.studiversity.database.type.timestampWithTimeZone
import com.studiversity.util.varcharMax
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import java.util.*

object StudyGroups : UUIDTable("study_group", "study_group_id") {
    val name = varcharMax("group_name")
    val specialtyId = optReference("specialty_id", Specialties.id)
    val createdAt = timestampWithTimeZone("created_at").defaultExpression(NowTimestamp())
    val updatedAt = timestampWithTimeZone("updated_at").nullable()
    val startAcademicYear = integer("start_academic_year")
    val endAcademicYear = integer("end_academic_year")
}

class StudyGroupDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<StudyGroupDao>(StudyGroups)

    var name by StudyGroups.name
    var specialty by SpecialtyDao optionalReferencedOn StudyGroups.specialtyId
    var createdAt by StudyGroups.createdAt
    var updatedAt by StudyGroups.updatedAt
    var startAcademicYear by StudyGroups.startAcademicYear
    var endAcademicYear by StudyGroups.endAcademicYear
}