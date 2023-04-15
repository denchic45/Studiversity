package com.denchic45.kts.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class UiInteractor<T>(private val state: T) {

    private val _stateFlow = MutableStateFlow(state)
    val stateFlow = _stateFlow.asStateFlow()

    fun set(state: T) {
        _stateFlow.value = (state)
    }

    fun update(function: (T) -> T) {
        _stateFlow.update { function(it) }
    }
}