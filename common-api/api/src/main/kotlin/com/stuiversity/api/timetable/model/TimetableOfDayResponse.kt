package com.stuiversity.api.timetable.model

import kotlinx.serialization.Serializable

@Serializable
data class TimetableOfDayResponse(
    val periods: List<PeriodResponse>
)
