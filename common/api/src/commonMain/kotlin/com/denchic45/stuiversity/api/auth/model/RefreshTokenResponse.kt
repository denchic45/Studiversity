package com.denchic45.stuiversity.api.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponse(
    val accessToken:String,
    val refreshToken:String,
    val userId:String
)
