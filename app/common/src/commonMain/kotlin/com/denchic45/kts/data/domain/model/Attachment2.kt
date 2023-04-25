package com.denchic45.kts.data.domain.model

import com.denchic45.kts.util.Files
import okio.Path
import java.util.*

sealed interface Attachment2 {
    val id: UUID
}

data class FileAttachment2(
    override val id: UUID,
    val name: String,
    val path: Path,
    val state: FileState
) : Attachment2

sealed class FileState {
    object Preview : FileState()
    object Downloading : FileState()
    object Downloaded : FileState()

    object FailDownload : FileState()
}

data class LinkAttachment2(override val id: UUID, val url: String) : Attachment2