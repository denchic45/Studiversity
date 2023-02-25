package com.denchic45.kts.data.domain.model

import com.denchic45.kts.util.Files
import com.denchic45.kts.util.getExtension
import java.io.File

sealed interface Attachment

data class AttachmentFile(val file: File) : Attachment {
    val shortName: String = Files.nameWithoutTimestamp(file.name)
    val name: String = file.name
    val extension: String = file.getExtension()
}

data class AttachmentLink(val url: String) : Attachment