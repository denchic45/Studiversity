package com.denchic45.studiversity.api.timetable.model

import com.denchic45.studiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PutTimetableOfDayRequest(
    @Serializable(UUIDSerializer::class)
    val studyGroupId: UUID,
    val periods: List<PeriodRequest>,
)