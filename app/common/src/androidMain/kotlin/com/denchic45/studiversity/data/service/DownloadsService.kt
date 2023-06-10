package com.denchic45.studiversity.data.service

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.denchic45.studiversity.data.db.local.source.AttachmentLocalDataSource
import com.denchic45.studiversity.data.domain.model.FileState
import com.denchic45.studiversity.data.workmanager.DownloadWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import java.util.UUID

actual class DownloadsService actual constructor() {
    lateinit var context: Context
    lateinit var attachmentLocalDataSource: AttachmentLocalDataSource

    @Inject
    @javax.inject.Inject
    constructor(context: Context, attachmentLocalDataSource: AttachmentLocalDataSource) : this() {
        this.context = context
        this.attachmentLocalDataSource = attachmentLocalDataSource
    }

    private val workManager by lazy { WorkManager.getInstance(context) }

    actual fun download(attachmentId: UUID): Flow<FileState> {
        val request = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(workDataOf("id" to attachmentId.toString()))
            .build()
        workManager.enqueueUniqueWork(
            attachmentId.toString(),
            ExistingWorkPolicy.KEEP,
            request
        )
        return getDownloading(attachmentId)
    }

    actual fun getDownloading(attachmentId: UUID): Flow<FileState> {
        return workManager.getWorkInfosForUniqueWorkLiveData(attachmentId.toString()).asFlow()
            .map {
                it.lastOrNull()?.let { workInfo ->
                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED,
                        WorkInfo.State.RUNNING,
                        -> FileState.Downloading

                        WorkInfo.State.SUCCEEDED -> {
                            attachmentLocalDataSource.updateSync(attachmentId.toString(), true)
                            FileState.Downloaded
                        }

                        WorkInfo.State.FAILED,
                        WorkInfo.State.BLOCKED,
                        WorkInfo.State.CANCELLED,
                        -> FileState.FailDownload
                    }
                } ?: FileState.Preview
            }
    }

    actual fun cancel(attachmentId: UUID) {
        workManager.cancelUniqueWork(attachmentId.toString())
    }
}