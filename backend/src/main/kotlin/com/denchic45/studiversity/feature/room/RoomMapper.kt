package com.denchic45.studiversity.feature.room

import com.denchic45.studiversity.database.table.RoomDao
import com.denchic45.stuiversity.api.room.model.RoomResponse

fun RoomDao.toResponse() = RoomResponse(id.value, name, shortname)