package com.denchic45.kts.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SignInWithPasswordRequest(
    val email: String,
    val password: String,
    val returnSecureToken: Boolean = true
)