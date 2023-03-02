package com.denchic45.kts.data.service

import com.denchic45.kts.data.storage.Storage
import kotlinx.coroutines.flow.Flow
import java.util.*

expect class DownloadsService() {
     inline fun <reified T:Storage> download(attachmentId: UUID): Flow<DownloadState>
}