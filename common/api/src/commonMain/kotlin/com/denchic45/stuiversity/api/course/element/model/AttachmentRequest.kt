package com.denchic45.stuiversity.api.course.element.model

import kotlinx.serialization.Serializable

sealed interface AttachmentRequest

@Serializable
class CreateFileRequest(
    val name: String,
    val bytes: ByteArray,
) : AttachmentRequest

@Serializable
data class CreateLinkRequest(val url: String) : AttachmentRequest