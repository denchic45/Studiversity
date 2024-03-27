package com.denchic45.studiversity.api.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponse(
    val accessToken:String,
    val refreshToken:String,
    val userId:String
)
