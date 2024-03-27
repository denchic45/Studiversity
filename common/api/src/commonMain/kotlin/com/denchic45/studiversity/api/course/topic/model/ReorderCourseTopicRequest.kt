package com.denchic45.studiversity.api.course.topic.model

import kotlinx.serialization.Serializable

@Serializable
data class ReorderCourseTopicRequest(
    val order: Int
)