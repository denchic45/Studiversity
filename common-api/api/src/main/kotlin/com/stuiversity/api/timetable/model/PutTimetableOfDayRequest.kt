package com.stuiversity.api.timetable.model

import com.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PutTimetableOfDayRequest(
    @Serializable(UUIDSerializer::class)
    val studyGroupId: UUID,
    val periods: List<PeriodRequest>,
)