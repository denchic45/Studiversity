package com.denchic45.stuiversity.api.timetable.model

import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
sealed interface PeriodDetails

@Serializable
data class LessonDetails(
    @Serializable(UUIDSerializer::class)
    val courseId: UUID,
    val subject: SubjectResponse
) : PeriodDetails

@Serializable
data class EventDetails(
    val name: String,
    val color: String,
    val iconUrl: String
) : PeriodDetails