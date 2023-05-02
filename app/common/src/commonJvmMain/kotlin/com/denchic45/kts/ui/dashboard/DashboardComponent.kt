package com.denchic45.kts.ui.dashboard

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class DashboardComponent(
    @Assisted
    componentContext: ComponentContext
):ComponentContext by componentContext {
}