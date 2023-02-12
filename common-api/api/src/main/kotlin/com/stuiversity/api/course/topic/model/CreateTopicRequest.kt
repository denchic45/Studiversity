package com.stuiversity.api.course.topic.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateTopicRequest(
    val name: String
)
