package com.denchic45.stuiversity.api.course.work.model


import com.denchic45.stuiversity.util.LocalDateSerializer
import com.denchic45.stuiversity.util.LocalTimeSerializer
import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.util.*


@Serializable
data class CreateCourseWorkRequest(
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