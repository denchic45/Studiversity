package com.stuiversity.api.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpGoTrueRequest(
    val email: String,
    val password: String
)
