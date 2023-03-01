package com.denchic45.kts.data.workmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.denchic45.kts.data.storage.Storage

class DownloadWorkerFactory(private val storage: Storage) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): DownloadWorker = DownloadWorker(appContext, workerParameters, storage)
}