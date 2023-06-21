package com.denchic45.studiversity.data.service

import com.denchic45.studiversity.data.storage.AttachmentStorage
import com.github.michaelbull.result.fold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class DownloadWorker(
    private val coroutineScope: CoroutineScope,
    private val storage: AttachmentStorage,
    val id: UUID
) {
    val state = MutableStateFlow(State.DOWNLOADING)

    operator fun invoke() {
        coroutineScope.launch {
            state.value = State.DOWNLOADING
            storage.downloadAndSave(id)
                .fold(
                    success = { state.value = State.DOWNLOADING },
                    failure = { state.value = State.FAILED }
                )
        }
    }

    fun cancel() {
        coroutineScope.cancel()
    }

    enum class State { DOWNLOADING, SUCCESS, FAILED }
}