package com.denchic45.kts.data.service

import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.data.storage.AttachmentStorage
import kotlinx.coroutines.flow.Flow
import java.util.*

actual class DownloadsService actual constructor() {
    actual inline fun <reified T : AttachmentStorage> download(attachmentId: UUID): Flow<FileState> {
        TODO("Not yet implemented")
    }
}