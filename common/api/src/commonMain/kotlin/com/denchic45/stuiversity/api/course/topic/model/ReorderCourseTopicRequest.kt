package com.denchic45.stuiversity.api.course.topic.model

import kotlinx.serialization.Serializable

@Serializable
data class ReorderCourseTopicRequest(
    val order: Int
)