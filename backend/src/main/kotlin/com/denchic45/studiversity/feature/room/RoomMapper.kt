package com.denchic45.studiversity.feature.room

import com.denchic45.studiversity.database.table.RoomDao
import com.denchic45.studiversity.api.room.model.RoomResponse

fun RoomDao.toResponse() = RoomResponse(id.value, name, shortname)