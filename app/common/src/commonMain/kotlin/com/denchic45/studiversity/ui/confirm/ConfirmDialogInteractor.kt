package com.denchic45.studiversity.ui.confirm

import com.denchic45.studiversity.di.AppScope
import com.denchic45.studiversity.ui.UiInteractor
import kotlinx.coroutines.channels.Channel
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class ConfirmDialogInteractor(
    state: ConfirmState? = null
) : UiInteractor<ConfirmState?>(state) {
    private val confirmation = Channel<Boolean>()

    suspend fun onConfirm(confirm: Boolean) {
        confirmation.send(confirm)
        set(null)
    }

    suspend fun receiveConfirm(): Boolean {
        return confirmation.receive()
    }

    suspend fun confirmRequest(state: ConfirmState): Boolean {
        set(state)
        return receiveConfirm()
    }
}