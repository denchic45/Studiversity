package com.denchic45.studiversity.api.course.work.model


import com.denchic45.studiversity.util.*
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime


@Serializable
data class UpdateCourseWorkRequest(
    @Serializable(OptionalPropertySerializer::class)
    val name: OptionalProperty<String> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val description: OptionalProperty<String?> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val dueDate: OptionalProperty<@Serializable(LocalDateSerializer::class)LocalDate?> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val dueTime:OptionalProperty<@Serializable(LocalTimeSerializer::class)LocalTime?>  = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val maxGrade: OptionalProperty<Short> = OptionalProperty.NotPresent,
)