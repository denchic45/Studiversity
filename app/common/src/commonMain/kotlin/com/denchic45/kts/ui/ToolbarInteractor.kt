package com.denchic45.kts.ui

import com.denchic45.kts.di.AppScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class ToolbarInteractor {
     private val _titleFlow = MutableStateFlow<UiText>(UiText.StringText(""))
     val titleFlow = _titleFlow.asStateFlow()

    val dropdown = MutableStateFlow<List<DropdownMenuItem>>(emptyList())

    var title: UiText
        get() = _titleFlow.value
        set(value) = _titleFlow.update { value }

    private var dropdownObserver: (DropdownMenuItem) -> Unit = {}

    @Suppress("UNCHECKED_CAST")
    fun onDropDownClick(lambda: (DropdownMenuItem) -> Unit) {
        dropdownObserver = lambda
    }
}

fun main() {
    ToolbarInteractor().onDropDownClick {

    }
}