package com.denchic45.studiversity.data.mapper

import com.denchic45.studiversity.SectionEntity
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.util.toUUID
import java.util.*

fun TopicResponse.toEntity(courseId: UUID) = SectionEntity(
    section_id = id.toString(),
    course_id = courseId.toString(),
    name = name,
    order = order
)

fun List<TopicResponse>.toTopicEntities(courseId: UUID) = map { it.toEntity(courseId) }

fun SectionEntity.toResponse() = TopicResponse(
    id = section_id.toUUID(),
    name = name,
    order = order
)

fun List<SectionEntity>.toTopicResponses() = map { it.toResponse() }