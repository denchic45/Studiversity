package com.studiversity.feature.course.topic

import com.studiversity.database.table.CourseTopicDao
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse

fun CourseTopicDao.toResponse() = TopicResponse(
    id = id.value,
    name = name,
    order = order
)