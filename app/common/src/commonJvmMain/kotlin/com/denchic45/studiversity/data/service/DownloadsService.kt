package com.denchic45.studiversity.data.service

import com.denchic45.studiversity.data.domain.model.FileState
import kotlinx.coroutines.flow.Flow
import java.util.*

expect class DownloadsService() {
    fun download(attachmentId: UUID): Flow<FileState>

    fun getDownloading(attachmentId: UUID): Flow<FileState>

    fun cancel(attachmentId: UUID)
}