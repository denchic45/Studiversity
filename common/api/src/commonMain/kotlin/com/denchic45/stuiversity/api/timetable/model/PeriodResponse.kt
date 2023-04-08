package com.denchic45.stuiversity.api.timetable.model

import com.denchic45.stuiversity.api.room.model.RoomResponse
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
    val studyGroup: StudyGroupName
    val details: PeriodDetails
    val room: RoomResponse?
    val members: List<PeriodMember>

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
    override val order: Int,
    override val room: RoomResponse?,
    override val studyGroup: StudyGroupName,
    override val members: List<PeriodMember>,
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
    override val order: Int,
    override val room: RoomResponse?,
    override val studyGroup: StudyGroupName,
    override val members: List<PeriodMember>,
    override val details: EventDetails
) : PeriodResponse {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    override val type: PeriodType = PeriodType.EVENT
}

@Serializable
data class StudyGroupName(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val name: String
)

@Serializable
data class PeriodMember(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val firstName: String,
    val surname: String,
    val avatarUrl: String
) {
    val fullName: String
        get() = "$firstName $surname"
}