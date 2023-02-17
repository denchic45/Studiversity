package com.denchic45.stuiversity.api.schedule.model

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(val periods: List<Period>, val lunch: Period?)

@Serializable
data class Period(val start: String, val end: String)