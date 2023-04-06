package com.denchic45.stuiversity.api.course.element.model

import kotlinx.serialization.Serializable
import java.io.File

sealed interface AttachmentRequest

@Serializable
class CreateFileRequest(
    val name: String,
    val bytes: ByteArray,
) : AttachmentRequest {
    companion object {
        operator fun invoke(file: File): CreateFileRequest {
            return CreateFileRequest(file.name, file.readBytes())
        }

    }
}

@Serializable
data class CreateLinkRequest(val url: String) : AttachmentRequest