package com.denchic45.stuiversity.api.timetable.model

import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.util.*

sealed interface PeriodModel {
    val order: Int
    val room: RoomResponse?
    val type: PeriodType
    val members: List<PeriodMember>
}

@Serializable(PeriodRequestSerializer::class)
sealed interface PeriodRequest : PeriodModel

@Serializable
data class LessonRequest(
    override val order: Int,
    override val room: RoomResponse?,
    override val members: List<PeriodMember>,
    @Serializable(UUIDSerializer::class)
    val courseId: UUID
) : PeriodRequest {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    override val type: PeriodType = PeriodType.LESSON
}

@Serializable
data class EventRequest(
    override val order: Int,
    override val room: RoomResponse?,
    override val members: List<PeriodMember>,
    val name: String,
    val color: String,
    val iconUrl: String
) : PeriodRequest {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    override val type: PeriodType = PeriodType.EVENT
}

enum class PeriodType { LESSON, EVENT }