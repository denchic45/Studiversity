package com.denchic45.studiversity.database.table


import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

object Events : IdTable<Long>("event") {
    override val id: Column<EntityID<Long>> = long("period_id").autoIncrement().entityId()
        .references(Periods.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(id)
    val name = text("event_name")
    val color = text("color")
    val icon = text("icon_url")
}

class EventDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<EventDao>(Events)

    var name by Events.name
    var color by Events.color
    var icon by Events.icon

    var period by PeriodDao referencedOn Events.id
}