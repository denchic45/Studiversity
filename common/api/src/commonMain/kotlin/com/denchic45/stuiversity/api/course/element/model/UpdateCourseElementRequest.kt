package com.denchic45.stuiversity.api.course.element.model

import com.denchic45.stuiversity.util.OptionalProperty
import com.denchic45.stuiversity.util.OptionalPropertySerializer
import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UpdateCourseElementRequest(
    @Serializable(OptionalPropertySerializer::class)
    val topicId: OptionalProperty<@Serializable(UUIDSerializer::class) UUID?> = OptionalProperty.NotPresent
)