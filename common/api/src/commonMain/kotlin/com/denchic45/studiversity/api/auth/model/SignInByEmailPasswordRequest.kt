package com.denchic45.studiversity.api.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class SignInByEmailPasswordRequest(val email: String, val password: String)
