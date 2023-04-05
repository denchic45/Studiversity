package com.denchic45.kts.data.service

import com.denchic45.kts.data.domain.model.FileState
import kotlinx.coroutines.flow.Flow
import java.util.*

actual class DownloadsService @javax.inject.Inject actual constructor() {
    actual fun download(attachmentId: UUID): Flow<FileState> {
        TODO("Not yet implemented")
    }

    actual fun getDownloading(attachmentId: UUID): Flow<FileState> {
        TODO("Not yet implemented")
    }

    actual fun cancel(attachmentId: UUID) {
    }
}