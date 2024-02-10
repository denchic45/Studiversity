package com.denchic45.studiversity.setup.model

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseSetupRequest(
    val url: String,
    val user: String,
    val password: String
)