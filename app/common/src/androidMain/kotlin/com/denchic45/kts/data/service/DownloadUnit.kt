package com.denchic45.kts.data.service

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.data.workmanager.DownloadTestWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

actual class DownloadUnit private actual constructor() {
    constructor (workRequest: WorkRequest) : this() {
        this.workRequest = workRequest
    }

    private lateinit var workRequest: WorkRequest
    private val _state = MutableSharedFlow<FileState>()
    actual val state: Flow<FileState> = _state


    actual companion object {
        actual fun startWork(): DownloadUnit {
            return DownloadUnit(OneTimeWorkRequestBuilder<DownloadTestWorker>().setInputData(workDataOf()).build())
        }

        actual fun get(fileName: String): DownloadUnit {
        }

    }
}