package com.denchic45.stuiversity.api.specialty.model

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class SpecialtyResponse(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val shortname: String?
)