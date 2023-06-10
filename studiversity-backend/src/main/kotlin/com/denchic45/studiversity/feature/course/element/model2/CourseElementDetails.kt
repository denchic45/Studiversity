package com.denchic45.studiversity.feature.course.element.model2

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CourseElementDetails(
    @Serializable(UUIDSerializer::class)
    val courseId: UUID,
    val name: String,
    val description: String? = null,
    @Serializable(UUIDSerializer::class)
    val topicId: UUID?,
    val order: Int? = null
)