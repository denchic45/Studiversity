package com.stuiversity.api.course.topic.model

import com.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class TopicResponse(
    val id: @Serializable(UUIDSerializer::class) UUID,
    val name: String
)
