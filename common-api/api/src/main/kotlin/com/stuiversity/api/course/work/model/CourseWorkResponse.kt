package com.stuiversity.api.course.work.model

import com.stuiversity.util.LocalDateSerializer
import com.stuiversity.util.LocalTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class CourseWorkResponse(
    @Serializable(LocalDateSerializer::class)
    val dueDate: LocalDate?,
    @Serializable(LocalTimeSerializer::class)
    val dueTime: LocalTime?,
    val workType: CourseWorkType,
    val maxGrade: Short
)