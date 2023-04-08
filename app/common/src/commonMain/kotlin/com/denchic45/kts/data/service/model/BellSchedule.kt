package com.denchic45.kts.data.service.model

import kotlinx.serialization.Serializable

@Serializable
data class BellSchedule(
    val periods: List<BellPeriod>,
    val launch: BellPeriod? = null,
)

@Serializable
data class BellPeriod(val start: String, val end: String)