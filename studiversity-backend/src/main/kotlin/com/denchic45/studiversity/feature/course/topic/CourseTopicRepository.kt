package com.denchic45.studiversity.feature.course.topic

import com.denchic45.studiversity.database.table.CourseElementDao
import com.denchic45.studiversity.database.table.CourseElements
import com.denchic45.studiversity.database.table.CourseTopicDao
import com.denchic45.studiversity.database.table.CourseTopics
import com.denchic45.stuiversity.api.course.topic.RelatedTopicElements
import com.denchic45.stuiversity.api.course.topic.model.CreateTopicRequest
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.api.course.topic.model.UpdateTopicRequest
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.update
import java.util.*

class CourseTopicRepository {

    fun add(courseId: UUID, createTopicRequest: CreateTopicRequest): TopicResponse {
        return CourseTopicDao.new {
            this.courseId = courseId
            this.name = createTopicRequest.name
            this.order = generateOrderByCourseId(courseId)
        }.toResponse()
    }

    private fun generateOrderByCourseId(courseId: UUID) = CourseTopicDao.getMaxOrderByCourseId(courseId) + 1

    fun update(courseId: UUID, topicId: UUID, updateTopicRequest: UpdateTopicRequest): TopicResponse? {
        return getById(topicId, courseId)?.apply {
            updateTopicRequest.name.ifPresent {
                name = it
            }
        }?.toResponse()
    }

    private fun getById(
        topicId: UUID,
        courseId: UUID
    ) = CourseTopicDao.find(CourseTopics.id eq topicId and (CourseTopics.courseId eq courseId))
        .singleOrNull()

    fun remove(courseId: UUID, topicId: UUID, relatedTopicElements: RelatedTopicElements): Unit? {
        when (relatedTopicElements) {
            RelatedTopicElements.DELETE -> {}
            RelatedTopicElements.CLEAR_TOPIC -> {
                CourseElements.update(where = { CourseElements.topicId eq topicId }) {
                    it[CourseElements.topicId] = null
                    it[order] = order + CourseElementDao.getMaxOrderByCourseIdAndTopicId(courseId, null)
                }
            }
        }
        return getById(topicId, courseId)?.delete()
    }

    fun findById(topicId: UUID, courseId: UUID): TopicResponse? {
        return getById(topicId, courseId)?.toResponse()
    }

    fun findByCourseId(courseId: UUID): List<TopicResponse> {
        return CourseTopicDao.find(CourseTopics.courseId eq courseId)
            .map(CourseTopicDao::toResponse)
    }
}