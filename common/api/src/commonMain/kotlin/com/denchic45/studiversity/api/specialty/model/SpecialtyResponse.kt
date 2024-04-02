package com.denchic45.studiversity.api.specialty.model

import com.denchic45.studiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class SpecialtyResponse(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val shortname: String?,
    val code: String?
)