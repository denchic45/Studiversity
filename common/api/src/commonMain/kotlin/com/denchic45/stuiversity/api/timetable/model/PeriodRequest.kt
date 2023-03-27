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
    val memberIds: List<UUID>
    val details: PeriodDetails
}

@Serializable(PeriodRequestSerializer::class)
sealed interface PeriodRequest : PeriodModel

@Serializable
data class LessonRequest(
    override val order: Int,
    override val room: RoomResponse?,
    override val memberIds: List<@Serializable(UUIDSerializer::class) UUID>,
    override val details: LessonDetails
) : PeriodRequest {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    override val type: PeriodType = PeriodType.LESSON
}

@Serializable
data class EventRequest(
    override val order: Int,
    override val room: RoomResponse?,
    override val memberIds: List<@Serializable(UUIDSerializer::class) UUID>,
    override val details: EventDetails
) : PeriodRequest {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    override val type: PeriodType = PeriodType.EVENT
}

enum class PeriodType { LESSON, EVENT }