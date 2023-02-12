package com.stuiversity.api.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val firstName: String,
    val surname: String,
    val patronymic: String? = null,
    val email: String
)