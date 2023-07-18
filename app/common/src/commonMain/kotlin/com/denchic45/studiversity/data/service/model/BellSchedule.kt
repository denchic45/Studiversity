package com.denchic45.studiversity.data.service.model

import kotlinx.serialization.Serializable

@Serializable
data class BellSchedule(
    val periods: List<PeriodTime>,
    val lunch: PeriodTime? = null,
)

@Serializable
data class PeriodTime(val start: String, val end: String) {
    val displayText = "$start - $end"
}