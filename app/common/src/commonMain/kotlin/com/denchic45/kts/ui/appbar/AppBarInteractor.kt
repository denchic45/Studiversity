package com.denchic45.kts.ui.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.denchic45.kts.di.AppScope
import com.denchic45.kts.ui.UiInteractor
import com.denchic45.kts.ui.uiTextOf
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class AppBarInteractor(state: AppBarState = AppBarState(uiTextOf(""))) : UiInteractor<AppBarState>(state) {
    var navigationIcon = mutableStateOf(NavigationIcon.TOGGLE)
    var actions by mutableStateOf<(@Composable RowScope.() -> Unit)?>({})

}

enum class NavigationIcon { TOGGLE, BACK }