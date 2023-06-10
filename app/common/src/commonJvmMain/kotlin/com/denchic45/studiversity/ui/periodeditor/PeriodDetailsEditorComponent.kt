package com.denchic45.studiversity.ui.periodeditor

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop


@Suppress("UNCHECKED_CAST")
abstract class PeriodDetailsEditorComponent<T : EditingPeriodDetails>(
    private val period: EditingPeriod,
    createDetails: () -> T,
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private var draftDetails: T = createDetails()

    val details: T
        get() = period.details as T

//    @Suppress("UNCHECKED_CAST")
//    val details: T = if (period.details.type == draftDetails.type)
//        period.details as T else draftDetails

    init {
        if (period.details.type == draftDetails.type) {
            draftDetails = period.details as T
        }
        lifecycle.doOnStart { period.details = draftDetails }
        lifecycle.doOnStop { draftDetails = period.details as T }
    }
}