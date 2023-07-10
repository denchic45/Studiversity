package com.denchic45.studiversity.data.service.model

import kotlinx.serialization.Serializable

@Serializable
data class BellSchedule(
    val periods: List<BellPeriod>,
    val lunch: BellPeriod? = null,
)

@Serializable
data class BellPeriod(val start: String, val end: String) {
    val displayText = "$start - $end"
}