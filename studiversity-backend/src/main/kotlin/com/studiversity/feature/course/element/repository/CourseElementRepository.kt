package com.studiversity.feature.course.element.repository

import com.studiversity.database.exists
import com.studiversity.database.table.*
import com.studiversity.feature.course.element.toCourseElementResponse
import com.studiversity.feature.course.element.toResponse
import com.studiversity.util.toSqlSortOrder
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import com.denchic45.stuiversity.api.course.element.model.CourseElementsSorting
import com.denchic45.stuiversity.api.course.element.model.UpdateCourseElementRequest
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.util.*

class CourseElementRepository {

    fun addWork(courseId: UUID, request: CreateCourseWorkRequest): CourseElementResponse {
        val elementId = UUID.randomUUID()
        return CourseElementDao.new(elementId) {
            this.courseId = courseId
            this.topicId = request.topicId
            this.name = request.name
            this.type = CourseElementType.WORK
            this.order = generateOrderByCourseAndTopicId(courseId, request.topicId)
        }.toResponse(
            CourseWorkDao.new(elementId) {
                this.dueDate = request.dueDate
                this.dueTime = request.dueTime
                this.type = request.workType
                this.maxGrade = request.maxGrade
            }
        )
    }

    private fun generateOrderByCourseAndTopicId(courseId: UUID, topicId: UUID?) =
        CourseElementDao.getMaxOrderByCourseIdAndTopicId(courseId, topicId) + 1


    fun findById(elementId: UUID): CourseElementResponse? {
        return CourseElementDao.findById(elementId)?.run {
            toResponse(getElementDetailsByIdAndType(elementId, type))
        }
    }


    fun findCourseIdByElementId(elementId: UUID): UUID? {
        return CourseElementDao.findById(elementId)?.courseId
    }

    fun remove(courseId: UUID, elementId: UUID): Boolean = CourseElementDao
        .find(CourseElements.courseId eq courseId and (CourseElements.id eq elementId))
        .singleOrNull()?.apply {
            decreaseElementOrdersByTopicIdAndGreaterElementOrder(topicId, order)
        }?.delete() != null

    fun findMaxGradeByWorkId(workId: UUID): Short {
        return CourseWorkDao.findById(workId)!!.maxGrade
    }

    fun findTypeByElementId(elementId: UUID): CourseElementType? {
        return CourseElements.select(CourseElements.id eq elementId)
            .singleOrNull()?.let { it[CourseElements.type] }
    }

    fun exist(courseId: UUID, elementId: UUID) = CourseElements.exists {
        CourseElements.id eq elementId and (CourseElements.courseId eq courseId)
    }

    fun update(
        courseId: UUID,
        elementId: UUID,
        updateCourseElementRequest: UpdateCourseElementRequest
    ): CourseElementResponse {
        val dao = CourseElementDao.findById(elementId)!!

        decreaseElementOrdersByTopicIdAndGreaterElementOrder(dao.topicId, dao.order)

        updateCourseElementRequest.topicId.ifPresent { topicId ->
            dao.order = generateOrderByCourseAndTopicId(courseId, topicId)
            dao.topicId = topicId
        }

        return dao.toResponse(getElementDetailsByIdAndType(elementId, dao.type))
    }

    private fun decreaseElementOrdersByTopicIdAndGreaterElementOrder(topicId: UUID?, order: Int) {
        CourseElements.update(where = { CourseElements.topicId eq topicId and (CourseElements.order greater order) }) {
            it[CourseElements.order] = CourseElements.order - 1
        }
    }

    private fun getElementDetailsByIdAndType(elementId: UUID, type: CourseElementType): CourseElementDetailsDao {
        return when (type) {
            CourseElementType.WORK -> CourseWorkDao.findById(elementId)!!
            CourseElementType.MATERIAL -> TODO()
        }
    }

    fun findElementsByCourseId(courseId: UUID, sorting: List<CourseElementsSorting>?): List<CourseElementResponse> {
        val query = CourseElements.select(CourseElements.courseId eq courseId)
        sorting?.forEach {
            when (it) {
                is CourseElementsSorting.TopicId -> {
                    query.adjustColumnSet { innerJoin(CourseTopics, { CourseElements.topicId }, { CourseTopics.id }) }
                        .orderBy(CourseTopics.order, it.order.toSqlSortOrder())
                }
            }
        }
        query.orderBy(CourseElements.order)
        return query.map {
            it.toCourseElementResponse(
                getElementDetailsByIdAndType(it[CourseElements.id].value, it[CourseElements.type])
            )
        }
    }
}