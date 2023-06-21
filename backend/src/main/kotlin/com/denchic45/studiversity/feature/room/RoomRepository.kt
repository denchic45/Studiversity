package com.denchic45.studiversity.feature.room

import com.denchic45.studiversity.database.table.RoomDao
import com.denchic45.studiversity.database.table.Rooms
import com.denchic45.stuiversity.api.room.model.CreateRoomRequest
import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.api.room.model.UpdateRoomRequest
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import java.util.*

class RoomRepository {

    fun add(createRoomRequest: CreateRoomRequest): RoomResponse {
        return RoomDao.new {
            name = createRoomRequest.name
            shortname = createRoomRequest.shortname
        }.toResponse()
    }

    fun findById(id: UUID): RoomResponse? {
        return RoomDao.findById(id)?.toResponse()
    }

    fun find(query: String): List<RoomResponse> = RoomDao.find(
        Rooms.name.lowerCase() like "%$query%" or (Rooms.shortname.lowerCase() like "%$query%")
    ).map(RoomDao::toResponse)

    fun update(id: UUID, updateRoomRequest: UpdateRoomRequest): RoomResponse? {
        return RoomDao.findById(id)?.apply {
            updateRoomRequest.name.ifPresent {
                name = it
            }
            updateRoomRequest.shortname.ifPresent {
                shortname = it
            }
        }?.toResponse()
    }

    fun remove(id: UUID): Boolean {
        return RoomDao.findById(id)?.delete() != null
    }
}