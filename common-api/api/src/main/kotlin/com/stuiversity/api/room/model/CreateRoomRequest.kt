package com.stuiversity.api.room.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateRoomRequest(val name:String)
