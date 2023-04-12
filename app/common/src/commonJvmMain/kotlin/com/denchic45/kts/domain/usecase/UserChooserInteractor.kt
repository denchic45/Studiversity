package com.denchic45.kts.domain.usecase


import kotlinx.coroutines.channels.Channel
import java.util.*
import javax.inject.Inject

class UserChooserInteractor @Inject constructor() {
    private val selected = Channel<UUID>()

    suspend fun receive(): UUID {
        return selected.receive()
    }

    suspend fun post(userId: UUID) {
        selected.send(userId)
    }
}