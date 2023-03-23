package com.denchic45.stuiversity.api.timetable.model

import com.denchic45.stuiversity.util.LocalDateSerializer
import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.*

@Serializable(PeriodResponseSerializer::class)
sealed interface PeriodResponse : PeriodModel {
    val id: Long
    val date: LocalDate
    val studyGroupId: UUID

    fun copy(): PeriodResponse = when (this) {
        is EventResponse -> copy(id)
        is LessonResponse -> copy(id)
    }
}

@Serializable
data class LessonResponse(
    override val id: Long,
    @Serializable(LocalDateSerializer::class)
    override val date: LocalDate,
    override val order: Short,
    @Serializable(UUIDSerializer::class)
    override val roomId: UUID?,
    @Serializable(UUIDSerializer::class)
    override val studyGroupId: UUID,
    override val memberIds: List<@Serializable(UUIDSerializer::class) UUID>,
    override val details: LessonDetails
) : PeriodResponse {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    override val type: PeriodType = PeriodType.LESSON
}

@Serializable
data class EventResponse(
    override val id: Long,
    @Serializable(LocalDateSerializer::class)
    override val date: LocalDate,
    override val order: Short,
    @Serializable(UUIDSerializer::class)
    override val roomId: UUID?,
    @Serializable(UUIDSerializer::class)
    override val studyGroupId: UUID,
    override val memberIds: List<@Serializable(UUIDSerializer::class) UUID>,
    override val details: EventDetails
) : PeriodResponse {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    override val type: PeriodType = PeriodType.EVENT
}