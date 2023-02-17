package com.denchic45.stuiversity.api.course.element.model

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.util.*

enum class AttachmentType { FILE, LINK }

enum class AttachmentOwner { SUBMISSION, COURSE_ELEMENT }

@Serializable(AttachmentSerializer::class)
sealed class AttachmentHeader {
    abstract val id: UUID
    abstract val type: AttachmentType
}

@Serializable
data class FileAttachmentHeader(
    @Serializable(UUIDSerializer::class)
    override val id: UUID,
    val fileItem: FileItem
) : AttachmentHeader() {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    override val type: AttachmentType = AttachmentType.FILE
}

@Serializable
data class FileItem(
    val name: String,
    val thumbnailUrl: String?
)

@Serializable
data class LinkAttachmentHeader(
    @Serializable(UUIDSerializer::class)
    override val id: UUID,
    val link: Link
) : AttachmentHeader() {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    override val type: AttachmentType = AttachmentType.LINK
}

@Serializable
data class Link(
    val url: String,
    val name: String,
    val thumbnailUrl: String?
) : Attachment