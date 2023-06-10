package com.denchic45.studiversity.ui.periodeditor

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class EventDetailsEditorComponent(
    @Assisted
    _state: EditingPeriod,
    @Assisted
    componentContext: ComponentContext,
) : PeriodDetailsEditorComponent<EditingPeriodDetails.Event>(
    _state,
    EditingPeriodDetails::Event,
    componentContext
) {

    fun onNameType(name: String) {
        details.name = name
    }
}