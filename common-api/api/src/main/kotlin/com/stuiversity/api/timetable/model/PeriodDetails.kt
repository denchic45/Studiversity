package com.stuiversity.api.timetable.model

import com.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
sealed interface PeriodDetails

@Serializable
data class LessonDetails(
    @Serializable(UUIDSerializer::class)
    val courseId: UUID
) : PeriodDetails

@Serializable
data class EventDetails(
    val name: String,
    val color: String,
    val icon: String
) : PeriodDetails