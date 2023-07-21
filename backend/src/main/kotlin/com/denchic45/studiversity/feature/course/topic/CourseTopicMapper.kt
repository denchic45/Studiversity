package com.denchic45.studiversity.feature.course.topic

import com.denchic45.studiversity.database.table.CourseTopicDao
import com.denchic45.stuiversity.api.course.topic.model.CourseTopicResponse

fun CourseTopicDao.toResponse() = CourseTopicResponse(
    id = id.value,
    courseId = courseId,
    name = name,
    order = order
)