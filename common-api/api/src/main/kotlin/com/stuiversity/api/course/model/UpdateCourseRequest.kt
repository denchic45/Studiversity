package com.stuiversity.api.course.model

import com.stuiversity.util.OptionalProperty
import com.stuiversity.util.OptionalPropertySerializer
import com.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UpdateCourseRequest(
    @Serializable(OptionalPropertySerializer::class)
    val name: OptionalProperty<String> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val subjectId: OptionalProperty<@Serializable(UUIDSerializer::class) UUID?> = OptionalProperty.NotPresent
)
