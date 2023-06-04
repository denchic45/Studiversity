package com.denchic45.stuiversity.api.auth.model

import com.denchic45.stuiversity.api.user.model.Gender
import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val firstName: String,
    val surname: String,
    val patronymic: String? = null,
    val gender: Gender,
    val email: String,
    val password: String,
)
