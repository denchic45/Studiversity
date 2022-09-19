package com.denchic45.kts.data.network.model

@kotlinx.serialization.Serializable
data class BellSchedule(
    val schedule: List<Pair<String, String>>, val zeroPeriod: Pair<String, String>? = null
)