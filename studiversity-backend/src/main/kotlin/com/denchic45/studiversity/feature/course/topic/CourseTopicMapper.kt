package com.denchic45.studiversity.feature.course.topic

import com.denchic45.studiversity.database.table.CourseTopicDao
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse

fun CourseTopicDao.toResponse() = TopicResponse(
    id = id.value,
    name = name,
    order = order
)