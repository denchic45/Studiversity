package com.denchic45.firebasemultiplatform.api

@kotlinx.serialization.Serializable
data class DocumentRequest(
    val name: String? = null,
    val fields: Map<String, Value>
)