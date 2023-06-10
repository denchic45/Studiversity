package com.denchic45.studiversity.ui.schedule

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class ScheduleComponent(
    @Assisted
    componentContext: ComponentContext) : ComponentContext by componentContext {
}