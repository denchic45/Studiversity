package com.denchic45.studiversity.api.auth.model

import com.denchic45.studiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SignInResponse(
    val token: String,
    val refreshToken: String,
    @Serializable(UUIDSerializer::class)
    val organizationId:UUID,
)
