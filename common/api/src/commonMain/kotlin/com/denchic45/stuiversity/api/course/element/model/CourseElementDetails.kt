package com.denchic45.stuiversity.api.course.element.model

import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
import com.denchic45.stuiversity.util.LocalDateSerializer
import com.denchic45.stuiversity.util.LocalTimeSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Serializable
sealed class CourseElementDetails

@Serializable
data class CourseWork @OptIn(ExperimentalSerializationApi::class) constructor(
    @Serializable(LocalDateSerializer::class)
    val dueDate: LocalDate?,
    @Serializable(LocalTimeSerializer::class)
    val dueTime: LocalTime?,
    val workType: CourseWorkType,
    val maxGrade: Short,
    @EncodeDefault
    val workDetails: CourseWorkDetails? = null
) : CourseElementDetails() {
    val late: Boolean
        get() = dueDate?.let {
            it.atTime(dueTime ?: LocalTime.MAX) < LocalDateTime.now()
        } ?: false
}

@Serializable
object CourseMaterial : CourseElementDetails()