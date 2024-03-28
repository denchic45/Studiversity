package com.denchic45.studiversity.api.account.model

import kotlinx.serialization.Serializable

@Serializable
data class AccountEmailResponse(
    val currentEmail: String,
    val needToConfirm: Boolean
)
