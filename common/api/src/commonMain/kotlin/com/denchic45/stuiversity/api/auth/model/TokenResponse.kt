package com.denchic45.stuiversity.api.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val token: String,
    val refreshToken: String
)
