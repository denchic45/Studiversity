package com.denchic45.kts.ui.periodeditor

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class EventDetailsEditorComponent(
    @Assisted
    _state: EditingPeriod,
    @Assisted
    componentContext: ComponentContext
) : PeriodDetailsEditorComponent<EditingPeriodDetails.Event>(_state, componentContext) {


}