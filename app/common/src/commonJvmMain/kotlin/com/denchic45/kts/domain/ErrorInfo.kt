package com.denchic45.kts.domain

import kotlinx.serialization.Serializable

@Serializable
data class Error(
    val errorCode: Int,
    val errorMessage: String,
    val status: String,
)
