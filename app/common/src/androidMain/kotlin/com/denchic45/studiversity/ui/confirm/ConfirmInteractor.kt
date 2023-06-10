package com.denchic45.studiversity.ui.confirm

import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfirmInteractor @Inject constructor() {
    private val confirmation = Channel<Boolean>()

    suspend fun onConfirm(confirm: Boolean) {
        confirmation.send(confirm)
    }

    suspend fun receiveConfirm(): Boolean {
        return confirmation.receive()
    }
}