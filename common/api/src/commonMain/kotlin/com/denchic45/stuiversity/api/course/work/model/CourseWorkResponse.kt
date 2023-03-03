package com.denchic45.stuiversity.api.course.work.model


import com.denchic45.stuiversity.api.course.element.model.CourseWorkDetails
import com.denchic45.stuiversity.util.LocalDateSerializer
import com.denchic45.stuiversity.util.LocalTimeSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class CourseWorkResponse(
    val name: String,
    val description:String,
    @Serializable(LocalDateSerializer::class)
    val dueDate: LocalDate?,
    @Serializable(LocalTimeSerializer::class)
    val dueTime: LocalTime?,
    val workType: CourseWorkType,
    @EncodeDefault
    val workDetails: CourseWorkDetails? = null,
    val maxGrade: Short
) {
    val late: Boolean
        get() = dueDate?.let {
            it.atTime(dueTime ?: LocalTime.MAX) > LocalDateTime.now()
        } ?: false
}