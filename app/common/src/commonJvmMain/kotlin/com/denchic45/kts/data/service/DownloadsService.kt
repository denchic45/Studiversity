package com.denchic45.kts.data.service

import com.denchic45.kts.data.domain.model.FileState
import kotlinx.coroutines.flow.Flow
import java.util.*

expect class DownloadsService() {
    fun download(attachmentId: UUID): Flow<FileState>

    fun getDownloading(attachmentId: UUID): Flow<FileState>

    fun cancel(attachmentId: UUID)
}