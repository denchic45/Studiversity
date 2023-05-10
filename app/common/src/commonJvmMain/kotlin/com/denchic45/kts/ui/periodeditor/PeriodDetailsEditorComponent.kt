package com.denchic45.kts.ui.periodeditor

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnStop


abstract class PeriodDetailsEditorComponent<T : EditingPeriodDetails>(
    period: EditingPeriod,
    createDetails: () -> T,
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {

    private var draftDetails: T = createDetails()

    @Suppress("UNCHECKED_CAST")
    val details: T = period.details as? T ?: draftDetails

    init {
        period.details = details
        lifecycle.doOnStop {
            draftDetails = details
        }
    }
}