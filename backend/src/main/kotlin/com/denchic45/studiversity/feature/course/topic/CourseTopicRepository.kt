package com.denchic45.studiversity.feature.course.topic

import com.denchic45.studiversity.database.table.CourseElementDao
import com.denchic45.studiversity.database.table.CourseElements
import com.denchic45.studiversity.database.table.CourseTopicDao
import com.denchic45.studiversity.database.table.CourseTopics
import com.denchic45.studiversity.logger.logger
import com.denchic45.stuiversity.api.course.topic.model.CourseTopicResponse
import com.denchic45.stuiversity.api.course.topic.model.CreateCourseTopicRequest
import com.denchic45.stuiversity.api.course.topic.model.UpdateCourseTopicRequest
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.update
import java.util.*

class CourseTopicRepository {

    fun add(courseId: UUID, request: CreateCourseTopicRequest): CourseTopicResponse {
        return CourseTopicDao.new {
            this.courseId = courseId
            this.name = request.name
            this.order = generateOrderByCourseId(courseId)
        }.toResponse()
    }

    private fun generateOrderByCourseId(courseId: UUID) = CourseTopicDao.getMaxOrderByCourseId(courseId) + 1

    fun update(courseId: UUID, topicId: UUID, request: UpdateCourseTopicRequest): CourseTopicResponse? {
        return getById(topicId, courseId)?.apply {
            request.name.ifPresent {
                name = it
            }
        }?.toResponse()
    }

    fun reorder(topicId: UUID, newOrder: Int): CourseTopicResponse? {
        return CourseTopicDao.findById(topicId)?.also {
            val oldOrder = it.order
            it.order = newOrder
            shiftTopicsOrdersWhereGreaterOrder(topicId, oldOrder, newOrder)
        }?.toResponse()
    }

    private fun shiftTopicsOrdersWhereGreaterOrder(courseId: UUID, oldOrder: Int, newOrder: Int) {
        val shiftUpper = oldOrder < newOrder
        logger.info("reorder topic. old: $oldOrder new: $newOrder")
        val body: CourseTopics.(UpdateStatement) -> Unit = if (shiftUpper) {
            { it[order] = order - 1 }
        } else {
            { it[order] = order + 1 }
        }
        val endOrder = if (shiftUpper) newOrder - 1 else newOrder + 1
        CourseTopics.update(
            where = {
                CourseTopics.courseId eq courseId and (CourseTopics.order.between(oldOrder, endOrder))
            }, body = body
        )
    }

    private fun getById(
        topicId: UUID,
        courseId: UUID
    ) = CourseTopicDao.find(CourseTopics.id eq topicId and (CourseTopics.courseId eq courseId))
        .singleOrNull()

    fun remove(courseId: UUID, topicId: UUID, withElements: Boolean): Unit? {
        if (!withElements) {
            CourseElements.update(where = { CourseElements.topicId eq topicId }) {
                it[CourseElements.topicId] = null
                it[order] = order + CourseElementDao.getMaxOrderByCourseIdAndTopicId(courseId, null)
            }
        }
        return getById(topicId, courseId)?.delete()
    }

    fun findById(topicId: UUID, courseId: UUID): CourseTopicResponse? {
        return getById(topicId, courseId)?.toResponse()
    }

    fun findCourseId(topicId: UUID): UUID? {
        return CourseTopicDao.findById(topicId)?.courseId
    }

    fun findByCourseId(courseId: UUID): List<CourseTopicResponse> {
        return CourseTopicDao.find(CourseTopics.courseId eq courseId)
            .map(CourseTopicDao::toResponse)
    }
}