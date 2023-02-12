package com.stuiversity.api.course.element.model

import com.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CourseElementResponse(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    @Serializable(UUIDSerializer::class)
    val courseId: UUID,
    val name: String,
    val description: String? = null,
    @Serializable(UUIDSerializer::class)
    val topicId: UUID?,
    val order: Int,
    val details: CourseElementDetails
)