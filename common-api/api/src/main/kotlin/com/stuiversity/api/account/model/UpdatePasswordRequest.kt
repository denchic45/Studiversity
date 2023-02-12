package com.stuiversity.api.account.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)
