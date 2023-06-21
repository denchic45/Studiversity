package com.denchic45.stuiversity.api.room.model

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class RoomResponse(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val shortname: String
)

val RoomResponse.displayName
    get() = shortname.takeIf(String::isNotBlank) ?: name
