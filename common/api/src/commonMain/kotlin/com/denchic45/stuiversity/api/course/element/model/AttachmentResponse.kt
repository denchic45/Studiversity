package com.denchic45.stuiversity.api.course.element.model

import kotlinx.serialization.Serializable

sealed interface AttachmentResponse

class FileAttachmentResponse(
    val bytes: ByteArray,
    val name: String,
) : AttachmentResponse

@Serializable
data class LinkAttachmentResponse(
    val url: String,
    val name: String,
    val thumbnailUrl: String?
) : AttachmentResponse