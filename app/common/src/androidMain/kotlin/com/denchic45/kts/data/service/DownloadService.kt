package com.denchic45.kts.data.service

import com.denchic45.kts.data.storage.Storage
import com.denchic45.kts.domain.EmptyResource

actual class DownloadService actual constructor(storage: Storage) {
    actual suspend fun download(): EmptyResource {

    }
}