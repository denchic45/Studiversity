package com.denchic45.studiversity.database.table


import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Rooms : UUIDTable("room", "room_id") {
    val name = text("room_name")
    val shortname = text("shortname")
}

class RoomDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<RoomDao>(Rooms)
    var name by Rooms.name
    var shortname by Rooms.shortname
}