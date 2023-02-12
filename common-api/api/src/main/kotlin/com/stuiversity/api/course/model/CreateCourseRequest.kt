package com.stuiversity.api.course.model

import com.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateCourseRequest(
    val name: String,
    @Serializable(UUIDSerializer::class)
    val subjectId: UUID? = null,
)