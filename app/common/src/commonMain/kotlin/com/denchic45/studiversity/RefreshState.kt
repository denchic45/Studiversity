package com.denchic45.studiversity

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class RefreshState {
    val refreshing = MutableStateFlow(false)
    val dataLoading = MutableSharedFlow<Unit>()

    suspend fun hiddenUpdate() {
        dataLoading.emit(Unit)
    }

    suspend fun refresh() {
        refreshing.value = true
        dataLoading.emit(Unit)
    }

    fun stop() {
        refreshing.value = false
    }
}