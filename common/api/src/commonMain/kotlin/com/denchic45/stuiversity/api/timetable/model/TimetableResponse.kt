package com.denchic45.stuiversity.api.timetable.model

import kotlinx.serialization.Serializable

@Serializable
data class TimetableResponse(
    val monday: List<PeriodResponse>,
    val tuesday: List<PeriodResponse>,
    val wednesday: List<PeriodResponse>,
    val thursday: List<PeriodResponse>,
    val friday: List<PeriodResponse>,
    val saturday: List<PeriodResponse>
) {
    val days: List<List<PeriodResponse>>
    get() = listOf(monday , tuesday , wednesday , thursday , friday , saturday)
}
