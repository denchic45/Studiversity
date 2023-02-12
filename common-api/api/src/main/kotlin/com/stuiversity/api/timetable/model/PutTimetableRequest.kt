package com.stuiversity.api.timetable.model

import com.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PutTimetableRequest(
    @Serializable(UUIDSerializer::class)
    val studyGroupId: UUID,
    val monday: List<PeriodRequest>,
    val tuesday: List<PeriodRequest>,
    val wednesday: List<PeriodRequest>,
    val thursday: List<PeriodRequest>,
    val friday: List<PeriodRequest>,
    val saturday: List<PeriodRequest> = emptyList()
)