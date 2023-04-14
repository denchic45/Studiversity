package com.denchic45.kts.ui.appbar

import com.denchic45.kts.di.AppScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class AppBarInteractor {

    private val _appBarState = MutableStateFlow(AppBarState())
    val appBarState = _appBarState.asStateFlow()

    fun updateState(appBarState: AppBarState) {
        _appBarState.value = (appBarState)
    }
}