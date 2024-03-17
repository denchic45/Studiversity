package com.denchic45.stuiversity.api.course.element.model

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.io.File
import java.io.InputStream
import java.util.*

sealed interface AttachmentRequest

class CreateFileRequest(
    val name: String,
    val inputStream: InputStream,
) : AttachmentRequest {
    companion object {
        operator fun invoke(file: File): CreateFileRequest {
            return CreateFileRequest(file.name, file.inputStream())
        }
    }
}

@Serializable
data class CreateLinkRequest(val url: String) : AttachmentRequest

@Serializable
data class UploadedAttachmentRequest(
    @Serializable(UUIDSerializer::class)
    val attachmentId: UUID
) : AttachmentRequest