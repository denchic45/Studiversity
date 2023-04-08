package com.denchic45.stuiversity.api.timetable.model

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.util.*

sealed interface PeriodModel {
    val order: Int
    val type: PeriodType
}

@Serializable(PeriodRequestSerializer::class)
sealed interface PeriodRequest : PeriodModel {
    val roomId: UUID?
    val memberIds: List<UUID>
}

@Serializable
data class LessonRequest(
    override val order: Int,
    @Serializable(UUIDSerializer::class)
    override val roomId: UUID?,
    override val memberIds: List<@Serializable(UUIDSerializer::class) UUID>,
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
    @Serializable(UUIDSerializer::class)
    override val roomId: UUID?,
    override val memberIds: List<@Serializable(UUIDSerializer::class) UUID>,
    val name: String,
    val color: String,
    val iconUrl: String
) : PeriodRequest {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    override val type: PeriodType = PeriodType.EVENT
}

enum class PeriodType { LESSON, EVENT }