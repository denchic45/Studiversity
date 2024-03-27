package com.denchic45.studiversity.api.course.work.model


import com.denchic45.studiversity.util.LocalDateSerializer
import com.denchic45.studiversity.util.LocalDateTimeSerializer
import com.denchic45.studiversity.util.LocalTimeSerializer
import com.denchic45.studiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Serializable
data class CourseWorkResponse(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val description: String?,
    @Serializable(LocalDateSerializer::class)
    val dueDate: LocalDate?,
    @Serializable(LocalTimeSerializer::class)
    val dueTime: LocalTime?,
    val workType: CourseWorkType,
    val maxGrade: Short,
    @Serializable(UUIDSerializer::class)
    val courseId: UUID,
    @Serializable(UUIDSerializer::class)
    val topicId: UUID?,
    val submitAfterDueDate: Boolean?,
    @Serializable(LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime?
) {
    val late: Boolean
        get() = dueDate?.let {
            it.atTime(dueTime ?: LocalTime.MAX) < LocalDateTime.now()
        } ?: false
}