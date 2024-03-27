package com.denchic45.studiversity.api.room.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateRoomRequest(val name: String, val shortname: String)
