package com.denchic45.stuiversity.api.course.material.model

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
data class CreateCourseMaterialRequest(
    val name: String,
    val description: String? = null,
    @Serializable(UUIDSerializer::class)
    val topicId: UUID? = null,
)