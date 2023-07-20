package com.denchic45.studiversity.data.service

import com.denchic45.studiversity.data.db.local.source.AttachmentLocalDataSource
import com.denchic45.studiversity.data.storage.AttachmentStorage
import com.denchic45.studiversity.domain.model.FileState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import java.util.UUID

actual class DownloadsService actual constructor() {
    lateinit var attachmentLocalDataSource: AttachmentLocalDataSource
    lateinit var attachmentStorage: AttachmentStorage
    private lateinit var coroutineScope: CoroutineScope

    @Inject
    constructor(
        attachmentLocalDataSource: AttachmentLocalDataSource,
        attachmentStorage: AttachmentStorage,
        coroutineScope: CoroutineScope
    ) : this() {
        this.attachmentLocalDataSource = attachmentLocalDataSource
        this.attachmentStorage = attachmentStorage
        this.coroutineScope = coroutineScope
    }

    private val workers = mutableMapOf<UUID, DownloadWorker>()

    actual fun download(attachmentId: UUID): Flow<FileState> {
        workers[attachmentId] = DownloadWorker(coroutineScope, attachmentStorage, attachmentId)
        return getDownloading(attachmentId)
    }

    actual fun getDownloading(attachmentId: UUID): Flow<FileState> {
        return workers[attachmentId]?.state?.map {
            when (it) {
                DownloadWorker.State.DOWNLOADING -> FileState.Downloading
                DownloadWorker.State.SUCCESS -> {
                    attachmentLocalDataSource.updateSync(attachmentId.toString(), true)
                    FileState.Downloaded
                }

                DownloadWorker.State.FAILED -> FileState.FailDownload
            }
        } ?: flowOf(FileState.Preview)
    }

    actual fun cancel(attachmentId: UUID) {
        workers[attachmentId]?.cancel()
    }
}