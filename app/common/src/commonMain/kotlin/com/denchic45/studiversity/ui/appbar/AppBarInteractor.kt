package com.denchic45.studiversity.ui.appbar

import androidx.compose.runtime.mutableStateOf
import com.denchic45.studiversity.di.AppScope
import com.denchic45.studiversity.ui.UiInteractor
import com.denchic45.studiversity.ui.appbar2.NavigationIcon
import com.denchic45.studiversity.ui.uiTextOf
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class AppBarInteractor(state: AppBarState = AppBarState(uiTextOf(""))) : UiInteractor<AppBarState>(state) {
    var navigationIcon = mutableStateOf(NavigationIcon.TOGGLE)
//    var actions by mutableStateOf<(@Composable RowScope.() -> Unit)?>({})

}

