package com.denchic45.studiversity.data.mapper

import com.denchic45.studiversity.entity.CourseTopic
import com.denchic45.stuiversity.api.course.topic.model.CourseTopicResponse
import com.denchic45.stuiversity.util.toUUID

fun CourseTopicResponse.toEntity() = CourseTopic(
    topic_id = id.toString(),
    course_id = courseId.toString(),
    name = name,
    order = order
)

fun List<CourseTopicResponse>.toTopicEntities() = map(CourseTopicResponse::toEntity)

fun CourseTopic.toResponse() = CourseTopicResponse(
    id = topic_id.toUUID(),
    courseId = course_id.toUUID(),
    name = name,
    order = order
)

fun List<CourseTopic>.toTopicResponses() = map { it.toResponse() }