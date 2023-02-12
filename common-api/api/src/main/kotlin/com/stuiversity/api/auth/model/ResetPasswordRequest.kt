package com.stuiversity.api.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(val email: String)
