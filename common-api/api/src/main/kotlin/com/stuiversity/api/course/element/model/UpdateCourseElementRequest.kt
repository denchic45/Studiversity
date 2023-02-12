package com.stuiversity.api.course.element.model

import com.stuiversity.util.OptionalProperty
import com.stuiversity.util.OptionalPropertySerializer
import com.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UpdateCourseElementRequest(
    @Serializable(OptionalPropertySerializer::class)
    val topicId: OptionalProperty<@Serializable(UUIDSerializer::class) UUID?> = OptionalProperty.NotPresent
)