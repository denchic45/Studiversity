package com.studiversity.feature.room

import com.studiversity.database.table.RoomDao
import com.denchic45.stuiversity.api.room.model.CreateRoomRequest
import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.api.room.model.UpdateRoomRequest
import java.util.*

class RoomRepository {

    fun add(createRoomRequest: CreateRoomRequest): RoomResponse {
        return RoomDao.new {
            name = createRoomRequest.name
        }.toResponse()
    }

    fun findById(id: UUID): RoomResponse? {
        return RoomDao.findById(id)?.toResponse()
    }

    fun update(id: UUID, updateRoomRequest: UpdateRoomRequest): RoomResponse? {
        return RoomDao.findById(id)?.apply {
            updateRoomRequest.name.ifPresent {
                name = it
            }
        }?.toResponse()
    }

    fun remove(id: UUID): Boolean {
        return RoomDao.findById(id)?.delete() != null
    }
}