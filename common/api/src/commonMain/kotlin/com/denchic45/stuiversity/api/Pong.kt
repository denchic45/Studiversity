package com.denchic45.stuiversity.api

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
data class Pong(val organization: OrganizationResponse)

@Serializable
data class OrganizationResponse(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val name: String,
//    val allowRegistration: Boolean,
)