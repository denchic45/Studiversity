package com.denchic45.studiversity.api.timetable.model2

import com.denchic45.studiversity.util.LocalDateSerializer
import com.denchic45.studiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.*

@Serializable
data class ClassResponse(
    val order: Int,
    @Serializable(UUIDSerializer::class)
    val roomId: UUID?,
    @Serializable(LocalDateSerializer::class)
    val date: LocalDate,
    @Serializable(UUIDSerializer::class)
    val courseId: UUID,
    val teacherIds: List<@Serializable(UUIDSerializer::class) UUID>,
    val type: ClassType,
    val recurrence: ClassRecurrence
)

@Serializable
data class ClassRecurrence(
    val type: ClassRecurrenceType,
    @Serializable(LocalDateSerializer::class)
    val startAt: LocalDate,
    @Serializable(LocalDateSerializer::class)
    val endAt: LocalDate
)

enum class ClassType { LESSON, PRACTICE, LABORATORY, LECTURE }

enum class ClassRecurrenceType { ONCE, WEEKLY, BIWEEKLY }