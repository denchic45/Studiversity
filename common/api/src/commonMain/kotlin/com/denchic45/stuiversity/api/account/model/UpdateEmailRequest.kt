package com.denchic45.stuiversity.api.account.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateEmailRequest(val email: String)
