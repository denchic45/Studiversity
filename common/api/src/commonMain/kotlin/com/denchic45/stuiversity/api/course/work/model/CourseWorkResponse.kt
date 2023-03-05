package com.denchic45.stuiversity.api.course.work.model


import com.denchic45.stuiversity.api.course.element.model.CourseWorkDetails
import com.denchic45.stuiversity.util.LocalDateSerializer
import com.denchic45.stuiversity.util.LocalDateTimeSerializer
import com.denchic45.stuiversity.util.LocalTimeSerializer
import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class CourseWorkResponse(
    @Serializable(UUIDSerializer::class)
    val id:UUID,
    val name: String,
    val description: String,
    @Serializable(LocalDateSerializer::class)
    val dueDate: LocalDate?,
    @Serializable(LocalTimeSerializer::class)
    val dueTime: LocalTime?,
    val workType: CourseWorkType,
    @EncodeDefault
    val workDetails: CourseWorkDetails? = null,
    val maxGrade: Short,
    @Serializable(UUIDSerializer::class)
    val topicId: UUID,
    val submitAfterDueDate: Boolean,
    @Serializable(LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime
) {
    val late: Boolean
        get() = dueDate?.let {
            it.atTime(dueTime ?: LocalTime.MAX) > LocalDateTime.now()
        } ?: false
}