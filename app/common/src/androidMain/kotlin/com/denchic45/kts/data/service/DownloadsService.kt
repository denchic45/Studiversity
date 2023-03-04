package com.denchic45.kts.data.service

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.*
import com.denchic45.kts.data.db.local.source.AttachmentLocalDataSource
import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.data.storage.AttachmentStorage
import com.denchic45.kts.data.workmanager.DownloadTestWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import java.util.*

actual class DownloadsService actual constructor() {
    lateinit var context: Context
    lateinit var attachmentLocalDataSource: AttachmentLocalDataSource

    @Inject
    constructor(context: Context,attachmentLocalDataSource: AttachmentLocalDataSource) : this() {
        this.context = context
        this.attachmentLocalDataSource = attachmentLocalDataSource
    }

    val workManager = WorkManager.getInstance(context)

    actual inline fun <reified T : AttachmentStorage> download(attachmentId: UUID): Flow<FileState> {
        val request = OneTimeWorkRequestBuilder<DownloadTestWorker>()
            .setInputData(workDataOf("id" to attachmentId))
            .build()
        workManager.enqueueUniqueWork(
            attachmentId.toString(),
            ExistingWorkPolicy.KEEP,
            request
        ).result
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
}