package com.denchic45.kts.ui.confirm

import com.denchic45.kts.di.AppScope
import com.denchic45.kts.ui.UiInteractor
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
    }

    suspend fun receiveConfirm(): Boolean {
        return confirmation.receive()
    }
}