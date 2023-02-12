package com.stuiversity.api.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(val refreshToken:String)
