package com.studiversity.database.table

import com.studiversity.util.varcharMax
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Specialties : UUIDTable("specialty", "specialty_id") {
    val name = varcharMax("specialty_name")
}

class SpecialtyDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SpecialtyDao>(Specialties)

    var name by Specialties.name
}