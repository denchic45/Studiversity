package com.denchic45.kts.ui.appbar

import com.denchic45.kts.di.AppScope
import com.denchic45.kts.ui.UiInteractor
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class AppBarInteractor(state: AppBarState = AppBarState()) : UiInteractor<AppBarState>(state)