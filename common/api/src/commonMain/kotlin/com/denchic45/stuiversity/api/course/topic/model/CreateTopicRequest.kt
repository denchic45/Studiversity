package com.denchic45.stuiversity.api.course.topic.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateTopicRequest(
    val name: String
)
