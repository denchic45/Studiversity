package com.denchic45.stuiversity.api.user.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val firstName: String,
    val surname: String,
    val patronymic: String? = null,
    val email: String,
    val gender: Gender,
    val roleIds: List<Long>
)