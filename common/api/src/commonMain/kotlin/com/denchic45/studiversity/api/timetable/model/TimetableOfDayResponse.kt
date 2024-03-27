package com.denchic45.studiversity.api.timetable.model

import kotlinx.serialization.Serializable

@Serializable
data class TimetableOfDayResponse(
    val periods: List<PeriodResponse>
)
