package com.denchic45.stuiversity.api.auth.model

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SignInResponse(
    val token: String,
    val refreshToken: String,
    @Serializable(UUIDSerializer::class)
    val organizationId:UUID,
)
