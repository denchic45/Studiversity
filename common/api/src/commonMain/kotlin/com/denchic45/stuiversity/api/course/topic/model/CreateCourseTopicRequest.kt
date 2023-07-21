package com.denchic45.stuiversity.api.course.topic.model

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CreateCourseTopicRequest(
    val courseId: @Serializable(UUIDSerializer::class) UUID,
    val name: String
)
