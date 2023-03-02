package com.denchic45.stuiversity.api.course.work.model


import com.denchic45.stuiversity.util.LocalDateSerializer
import com.denchic45.stuiversity.util.LocalTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Serializable
data class CourseWorkResponse(
    val name: String,
    val description:String,
    @Serializable(LocalDateSerializer::class)
    val dueDate: LocalDate?,
    @Serializable(LocalTimeSerializer::class)
    val dueTime: LocalTime?,
    val workType: CourseWorkType,
    val maxGrade: Short
) {
    val late: Boolean
        get() = dueDate?.let {
            it.atTime(dueTime ?: LocalTime.MAX) > LocalDateTime.now()
        } ?: false
}