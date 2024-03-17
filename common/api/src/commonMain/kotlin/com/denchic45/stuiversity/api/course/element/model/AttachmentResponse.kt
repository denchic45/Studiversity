package com.denchic45.stuiversity.api.course.element.model

import java.io.InputStream
import java.util.*

//@Serializable(AttachmentSerializer::class)
//sealed interface AttachmentResponse {
//    val id:UUID
//}


class FileAttachmentResponse(
    val id: UUID,
    val inputStream: InputStream,
    val name: String,
)

//@Serializable
//data class LinkAttachmentResponse(
//    @Serializable(UUIDSerializer::class)
//    override val id: UUID,
//    val url: String,
//    val name: String,
//    val thumbnailUrl: String?
//) : AttachmentResponse