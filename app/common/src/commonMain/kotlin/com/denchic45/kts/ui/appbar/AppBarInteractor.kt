package com.denchic45.kts.ui.appbar

import androidx.compose.runtime.mutableStateOf
import com.denchic45.kts.di.AppScope
import com.denchic45.kts.ui.UiInteractor
import com.denchic45.kts.ui.appbar2.NavigationIcon
import com.denchic45.kts.ui.uiTextOf
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class AppBarInteractor(state: AppBarState = AppBarState(uiTextOf(""))) : UiInteractor<AppBarState>(state) {
    var navigationIcon = mutableStateOf(NavigationIcon.TOGGLE)
//    var actions by mutableStateOf<(@Composable RowScope.() -> Unit)?>({})

}

