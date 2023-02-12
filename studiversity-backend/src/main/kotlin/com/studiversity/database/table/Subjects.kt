package com.studiversity.database.table

import com.studiversity.util.varcharMax
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Subjects : UUIDTable("subject", "subject_id") {
    val name = varcharMax("subject_name")
    val iconName = varcharMax("icon_name")
}

class SubjectDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SubjectDao>(Subjects)

    var name by Subjects.name
    var iconName by Subjects.iconName
}