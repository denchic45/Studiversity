package com.denchic45.kts.data.service.model

import kotlinx.serialization.Serializable

@Serializable
data class SignInWithPasswordResponse(
    val idToken: String,
    val email: String,
    val refreshToken: String,
    val expiresIn: String,
    val localId: String,
    val registered: String
)