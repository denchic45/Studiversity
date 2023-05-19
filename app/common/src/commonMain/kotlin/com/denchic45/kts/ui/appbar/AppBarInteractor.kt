package com.denchic45.kts.ui.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.denchic45.kts.di.AppScope
import com.denchic45.kts.ui.UiInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class AppBarInteractor(state: AppBarState = AppBarState()) : UiInteractor<AppBarState>(state) {
    var navigationIcon = mutableStateOf(NavigationIcon.TOGGLE)
    var actions  by mutableStateOf<(@Composable RowScope.() -> Unit)?>({})
}

enum class NavigationIcon { TOGGLE, BACK }