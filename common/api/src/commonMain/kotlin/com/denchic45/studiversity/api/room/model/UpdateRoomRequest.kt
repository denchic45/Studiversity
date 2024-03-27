package com.denchic45.studiversity.api.room.model

import com.denchic45.studiversity.util.OptionalProperty
import com.denchic45.studiversity.util.OptionalPropertySerializer
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRoomRequest(
    @Serializable(OptionalPropertySerializer::class)
    val name: OptionalProperty<String> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val shortname: OptionalProperty<String> = OptionalProperty.NotPresent
)
