package com.stuiversity.api.room.model

import com.stuiversity.util.OptionalProperty
import com.stuiversity.util.OptionalPropertySerializer
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRoomRequest(
    @Serializable(OptionalPropertySerializer::class)
    val name: OptionalProperty<String>
)
