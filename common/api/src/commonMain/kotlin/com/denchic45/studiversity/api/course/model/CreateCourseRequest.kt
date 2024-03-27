package com.denchic45.studiversity.api.course.model

import com.denchic45.studiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateCourseRequest(
    val name: String,
    @Serializable(UUIDSerializer::class)
    val subjectId: UUID? = null,
)