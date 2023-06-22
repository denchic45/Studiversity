package com.denchic45.stuiversity.api.course.material.model


import com.denchic45.stuiversity.util.LocalDateTimeSerializer
import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class CourseMaterialResponse(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val description: String?,
    @Serializable(UUIDSerializer::class)
    val courseId: UUID,
    @Serializable(UUIDSerializer::class)
    val topicId: UUID?,
    @Serializable(LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime?
)