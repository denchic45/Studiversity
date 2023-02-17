package com.denchic45.stuiversity.api.timetable.model

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PutTimetableOfDayRequest(
    @Serializable(UUIDSerializer::class)
    val studyGroupId: UUID,
    val periods: List<PeriodRequest>,
)