package com.denchic45.firebasemultiplatform.api

@kotlinx.serialization.Serializable
data class Write(
    val updateMask: DocumentMask? = null,
    val updateTransforms: List<FieldTransform>? = null,
    // TODO currentDocument
    val update: DocumentRequest,
    val delete: String? = null
    // TODO transform
)