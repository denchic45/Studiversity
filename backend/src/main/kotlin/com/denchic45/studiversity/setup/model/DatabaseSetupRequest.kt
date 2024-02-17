package com.denchic45.studiversity.setup.model

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseSetupRequest(
    val host: String,
    val port: String,
    val name: String,
    val user: String,
    val password: String
)