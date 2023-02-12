package com.stuiversity.api.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class SignInByEmailPasswordRequest(val email: String, val password: String)
