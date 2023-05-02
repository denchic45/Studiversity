package com.denchic45.kts.ui.periodeditor

import com.arkivanov.decompose.ComponentContext


abstract class PeriodDetailsEditorComponent<T : EditingPeriodDetails>(
    period: EditingPeriod,
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {
    @Suppress("UNCHECKED_CAST")
    val details: T = period.details as T
}