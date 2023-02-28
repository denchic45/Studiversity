package com.denchic45.kts.data.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.denchic45.kts.data.storage.Storage

class DownloadWorker(
    context: Context,
    params: WorkerParameters,
    storage: Storage
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}