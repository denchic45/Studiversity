package com.denchic45.studiversity.data.domain.model

import com.denchic45.studiversity.util.Files
import com.denchic45.studiversity.util.getExtension
import okio.Path
import java.io.File

sealed interface Attachment

data class AttachmentFile(val file: Path) : Attachment {
    val shortName: String = Files.nameWithoutTimestamp(file.name)
    val name: String = file.name
    val extension: String = file.getExtension()
}

data class AttachmentLink(val url: String) : Attachment