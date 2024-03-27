package com.denchic45.studiversity.api.course.topic.model

import com.denchic45.studiversity.util.OptionalProperty
import com.denchic45.studiversity.util.OptionalPropertySerializer
import com.denchic45.studiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UpdateCourseTopicRequest(
    val courseId: @Serializable(UUIDSerializer::class) UUID,
    @Serializable(OptionalPropertySerializer::class)
    val name: OptionalProperty<String> = OptionalProperty.NotPresent
)
