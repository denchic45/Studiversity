package com.denchic45.kts.ui.fab

import com.denchic45.kts.di.AppScope
import com.denchic45.kts.ui.UiInteractor
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class FabInteractor(state: FabState = FabState(visible = false)) : UiInteractor<FabState>(state)