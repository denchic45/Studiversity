package com.denchic45.studiversity.data.workmanager

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.denchic45.studiversity.data.storage.AttachmentStorage

class DownloadWorkerFactory(private val attachmentStorage: AttachmentStorage) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): DownloadWorker = DownloadWorker(appContext, workerParameters, attachmentStorage)
}