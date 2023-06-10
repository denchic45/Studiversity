package com.denchic45.studiversity.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class UiInteractor<T>(state: T) {

    private val _stateFlow = MutableStateFlow(state)
    val stateFlow = _stateFlow.asStateFlow()

    val mutableSharedFlow = MutableSharedFlow<T>(replay = 1)

    var state by mutableStateOf(state)

    @Composable
    fun rememberState() = remember { state }

    fun set(state: T) {
        _stateFlow.value = (state)
        mutableSharedFlow.tryEmit(state)
        this.state = state
    }

    fun update(function: (T) -> T) {
        _stateFlow.update { function(it) }
        this.state = function(state)
    }
}