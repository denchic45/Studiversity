package com.denchic45.kts.data.domain.model

import com.denchic45.kts.util.Files
import com.denchic45.kts.util.getExtension
import okio.Path
import java.io.File

sealed interface Attachment2

data class Attachment2File( val path:Path, val state: FileState) : Attachment {
    val name: String = path.name
    val shortName: String = Files.nameWithoutTimestamp(name)
//    val extension: String = file.getExtension()
}

sealed class FileState {
    object Preview : FileState()
    object Downloading : FileState()
    object Downloaded : FileState()
}

data class Attachment2Link(val url: String) : Attachment