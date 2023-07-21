package com.denchic45.stuiversity.api.course.topic.model

import com.denchic45.stuiversity.util.OptionalProperty
import com.denchic45.stuiversity.util.OptionalPropertySerializer
import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UpdateCourseTopicRequest(
    val courseId: @Serializable(UUIDSerializer::class) UUID,
    @Serializable(OptionalPropertySerializer::class)
    val name: OptionalProperty<String> = OptionalProperty.NotPresent
)
