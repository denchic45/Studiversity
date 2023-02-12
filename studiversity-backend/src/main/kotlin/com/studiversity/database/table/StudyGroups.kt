package com.studiversity.database.table

import com.studiversity.database.table.SpecialtyDao.Companion.transform
import com.studiversity.database.type.array
import com.studiversity.util.varcharMax
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.IntegerColumnType
import java.util.*

object StudyGroups : UUIDTable("study_group", "study_group_id") {
    val name = varcharMax("group_name")
    val academicYear = array<Short>("academic_year", IntegerColumnType()).transform(
        toReal = { it.map(Short::toInt) },
        toColumn = { it.map(Int::toShort).toTypedArray() }
    )
    val specialtyId = optReference("specialty_id", Specialties.id)
}

class StudyGroupDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<StudyGroupDao>(StudyGroups)

    var name by StudyGroups.name
    var academicYear by StudyGroups.academicYear
    var specialty by SpecialtyDao optionalReferencedOn StudyGroups.specialtyId
}