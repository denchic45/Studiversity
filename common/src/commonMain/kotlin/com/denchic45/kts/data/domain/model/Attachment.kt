package com.denchic45.kts.data.domain.model

import com.denchic45.kts.util.Files
import com.denchic45.kts.util.getExtension
import java.io.File

data class Attachment(
    val file: File
) : DomainModel {

    val name: String = Files.nameWithoutTimestamp(file.name)

    override var id: String = name

    val extension: String = file.getExtension()
}