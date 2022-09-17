package com.denchic45.kts.data.network.model

@kotlinx.serialization.Serializable
data class BellSchedule(val startAtZero: Boolean, val schedule: List<Pair<String, String>>)