package com.denchic45.kts.ui.model

import com.denchic45.kts.data.domain.model.Attachment2
import com.denchic45.kts.data.domain.model.FileAttachment2
import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.data.domain.model.LinkAttachment2
import com.denchic45.kts.util.Files
import com.denchic45.kts.util.getExtension
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.course.element.model.CreateLinkRequest
import com.eygraber.uri.Uri
import okio.Path
import java.util.UUID

sealed interface AttachmentItem {
    val name: String
    val previewUrl: String?
    val attachmentId: UUID?

    data class FileAttachmentItem(
        override val name: String,
        override val previewUrl: String?,
        override val attachmentId: UUID?,
        val state: FileState,
        val path: Path
    ) : AttachmentItem {
        val shortName: String = Files.nameWithoutTimestamp(name)
        val extension: String = path.getExtension()
    }

    data class LinkAttachmentItem(
        override val name: String,
        override val previewUrl: String?,
        override val attachmentId: UUID?,
        val url: String
    ) : AttachmentItem
}

fun List<Attachment2>.toAttachmentItems(): List<AttachmentItem> {
    return map { attachment ->
        when (attachment) {
            is FileAttachment2 -> AttachmentItem.FileAttachmentItem(
                name = attachment.name,
                previewUrl = null,
                attachmentId = attachment.id,
                state = attachment.state,
//                path = attachment.path,
                path = attachment.path
            )

            is LinkAttachment2 -> AttachmentItem.LinkAttachmentItem(
                name = attachment.url,
                previewUrl = null,
                attachmentId = attachment.id,
                url = attachment.url
            )
        }
    }
}

fun AttachmentItem.toRequest(): AttachmentRequest = when (this) {
    is AttachmentItem.FileAttachmentItem -> CreateFileRequest(
        name = name,
        bytes = path.toFile().readBytes()
    )

    is AttachmentItem.LinkAttachmentItem -> CreateLinkRequest(url)
}

private fun Path.toUri(): Uri = Uri.parse(toString())
