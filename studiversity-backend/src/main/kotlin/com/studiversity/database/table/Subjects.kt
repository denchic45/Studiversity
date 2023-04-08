package com.studiversity.database.table

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Subjects : UUIDTable("subject", "subject_id") {
    val name = text("subject_name")
    val shortname = text("shortname")
    val iconUrl = text("icon_name")
}

class SubjectDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SubjectDao>(Subjects)

    var name by Subjects.name
    var shortname by Subjects.shortname
    var iconUrl by Subjects.iconUrl
}