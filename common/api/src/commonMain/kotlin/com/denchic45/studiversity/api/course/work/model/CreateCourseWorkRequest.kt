package com.denchic45.studiversity.api.course.work.model


import com.denchic45.studiversity.util.LocalDateSerializer
import com.denchic45.studiversity.util.LocalTimeSerializer
import com.denchic45.studiversity.util.UUIDSerializer
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