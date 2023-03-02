package com.denchic45.kts.data.service

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.denchic45.kts.data.storage.Storage
import com.denchic45.kts.data.workmanager.DownloadTestWorker
import kotlinx.coroutines.flow.Flow
import java.util.*

actual class DownloadsService actual constructor() {
    actual inline fun <reified T : Storage> download(attachmentId: UUID): Flow<DownloadState> {
            OneTimeWorkRequestBuilder<DownloadTestWorker>()
                .setInputData(workDataOf("id" to attachmentId))
                .build()
    }
}