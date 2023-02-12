package com.stuiversity.api.course.work.model

import com.stuiversity.util.LocalDateSerializer
import com.stuiversity.util.LocalTimeSerializer
import com.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.util.*


@Serializable
data class CreateCourseWorkRequest constructor(
    val name: String,
    val description: String? = null,
    @Serializable(UUIDSerializer::class)
    val topicId: UUID? = null,
    @Serializable(LocalDateSerializer::class)
    val dueDate: LocalDate? = null,
    @Serializable(LocalTimeSerializer::class)
    val dueTime: LocalTime? = null,
    val workType: CourseWorkType,
    val maxGrade: Short
)