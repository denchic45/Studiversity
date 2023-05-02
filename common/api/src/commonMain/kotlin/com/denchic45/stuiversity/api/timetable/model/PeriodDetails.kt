package com.denchic45.stuiversity.api.timetable.model

import com.denchic45.stuiversity.api.course.model.CourseResponse
import kotlinx.serialization.Serializable

@Serializable
sealed interface PeriodDetails

@Serializable
data class LessonDetails(
    val course: CourseResponse
) : PeriodDetails

@Serializable
data class EventDetails(
    val name: String,
    val color: String,
    val iconUrl: String
) : PeriodDetails