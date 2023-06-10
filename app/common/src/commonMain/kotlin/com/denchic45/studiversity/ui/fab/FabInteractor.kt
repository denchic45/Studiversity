package com.denchic45.studiversity.ui.fab

import com.denchic45.studiversity.di.AppScope
import com.denchic45.studiversity.ui.UiInteractor
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class FabInteractor(state: FabState = FabState(visible = false)) : UiInteractor<FabState>(state)