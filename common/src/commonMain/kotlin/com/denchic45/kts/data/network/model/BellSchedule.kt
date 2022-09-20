package com.denchic45.kts.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class BellSchedule(
    val schedule: List<Pair<String, String>>, val zeroPeriod: Pair<String, String>? = null
)