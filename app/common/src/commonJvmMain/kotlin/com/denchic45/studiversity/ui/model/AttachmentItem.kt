package com.denchic45.studiversity.ui.model

import com.denchic45.studiversity.api.course.element.model.AttachmentRequest
import com.denchic45.studiversity.api.course.element.model.CreateFileRequest
import com.denchic45.studiversity.api.course.element.model.CreateLinkRequest
import com.denchic45.studiversity.util.Files
import com.denchic45.studiversity.util.getExtension
import com.eygraber.uri.Uri
import okio.Path
import java.util.*

sealed interface AttachmentItem {
    val name: String
    val thumbnailUrl: String?
    val id: UUID

    data class FileAttachmentItem(
        override val name: String,
        override val thumbnailUrl: String?,
        override val id: UUID,
        val state: FileState,
        val path: Path
    ) : AttachmentItem {
        val shortName: String = Files.nameWithoutTimestamp(name)
        val extension: String = path.getExtension()
    }

    data class LinkAttachmentItem(
        override val name: String,
        override val thumbnailUrl: String?,
        val url: String,
        override val id: UUID
    ) : AttachmentItem
}

fun AttachmentItem.toRequest(): AttachmentRequest = when (this) {
    is AttachmentItem.FileAttachmentItem -> CreateFileRequest(
        name = name,
        inputStream = path.toFile().inputStream()
    )

    is AttachmentItem.LinkAttachmentItem -> CreateLinkRequest(url)
}

private fun Path.toUri(): Uri = Uri.parse(toString())

sealed class FileState {
    data object Preview : FileState()
    data object Downloading : FileState()
    data object Downloaded : FileState()
    data object FailDownload : FileState()
}