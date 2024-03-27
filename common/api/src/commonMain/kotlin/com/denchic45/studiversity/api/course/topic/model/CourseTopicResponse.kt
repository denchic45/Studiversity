package com.denchic45.studiversity.api.course.topic.model


import com.denchic45.studiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CourseTopicResponse(
    val id: @Serializable(UUIDSerializer::class) UUID,
    val courseId: @Serializable(UUIDSerializer::class) UUID,
    val name: String,
    val order: Int
)
