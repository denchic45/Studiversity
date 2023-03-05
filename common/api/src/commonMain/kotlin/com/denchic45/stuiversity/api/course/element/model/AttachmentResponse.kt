package com.denchic45.stuiversity.api.course.element.model

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

sealed interface AttachmentResponse {
    val id:UUID
}

@Serializable
class FileAttachmentResponse(
    @Serializable(UUIDSerializer::class)
    override val id: UUID,
    val bytes: ByteArray,
    val name: String,
) : AttachmentResponse

@Serializable
data class LinkAttachmentResponse(
    @Serializable(UUIDSerializer::class)
    override val id: UUID,
    val url: String,
    val name: String,
    val thumbnailUrl: String?
) : AttachmentResponse