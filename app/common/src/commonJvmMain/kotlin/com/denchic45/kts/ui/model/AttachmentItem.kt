package com.denchic45.kts.ui.model

import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.util.Files
import com.denchic45.kts.util.getExtension
import okio.Path
import java.io.File
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
        val file: Path
    ) : AttachmentItem {
        val shortName: String = Files.nameWithoutTimestamp(name)
        val extension: String = file.getExtension()
    }

    data class LinkAttachmentItem(
        override val name: String,
        override val previewUrl: String?,
        override val attachmentId: UUID?,
        val url: String
    ) : AttachmentItem
}