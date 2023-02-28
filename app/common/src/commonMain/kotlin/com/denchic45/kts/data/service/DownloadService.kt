package com.denchic45.kts.data.service

import com.denchic45.kts.data.storage.Storage
import com.denchic45.kts.domain.EmptyResource

expect class DownloadService(storage: Storage) {
    suspend fun download(): EmptyResource
}