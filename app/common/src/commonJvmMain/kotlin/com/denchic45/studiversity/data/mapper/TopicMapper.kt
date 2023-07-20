package com.denchic45.studiversity.data.mapper

import com.denchic45.studiversity.entity.CourseTopic
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.util.toUUID
import java.util.UUID

fun TopicResponse.toEntity(courseId: UUID) = CourseTopic(
    section_id = id.toString(),
    course_id = courseId.toString(),
    name = name,
    order = order
)

fun List<TopicResponse>.toTopicEntities(courseId: UUID) = map { it.toEntity(courseId) }

fun CourseTopic.toResponse() = TopicResponse(
    id = section_id.toUUID(),
    name = name,
    order = order
)

fun List<CourseTopic>.toTopicResponses() = map { it.toResponse() }