package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Specialties : UUIDTable("specialty", "specialty_id") {
    val name = text("specialty_name")
    val shortname = text("shortname").nullable()
    val code = text("code").nullable()
}

class SpecialtyDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SpecialtyDao>(Specialties)

    var name by Specialties.name
    var shortname by Specialties.shortname
    var code by Specialties.code
}