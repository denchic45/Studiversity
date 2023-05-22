package com.studiversity.feature.course.element

import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import com.denchic45.stuiversity.api.course.element.model.CourseElementsSorting
import com.denchic45.stuiversity.api.course.element.model.UpdateCourseElementRequest
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import com.studiversity.database.exists
import com.studiversity.database.table.*
import com.studiversity.feature.course.work.toWorkResponse
import com.studiversity.util.toSqlSortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.util.*

class CourseElementRepository {

    fun findById(elementId: UUID): CourseElementResponse? {
        return CourseElementDao.findById(elementId)?.run {
            toResponse(getElementDetailsByIdAndType(elementId, type))
        }
    }

    fun findWorkById(workId: UUID): CourseWorkResponse? {
        return CourseWorkDao.findById(workId)
//            ?.load(CourseWorkDao::dueDate, CourseWorkDao::dueTime, CourseWorkDao::type, CourseWorkDao::maxGrade)
            ?.let { CourseElementDao.findById(workId)?.toWorkResponse(it) }
    }

    fun findCourseIdByElementId(elementId: UUID): UUID? {
        return CourseElementDao.findById(elementId)?.course?.id?.value
    }

    fun remove(courseId: UUID, elementId: UUID): Boolean = CourseElementDao
        .find(CourseElements.courseId eq courseId and (CourseElements.id eq elementId))
        .singleOrNull()?.apply {
            decreaseElementOrdersByTopicIdAndGreaterElementOrder(topic?.id?.value, order)
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

        updateCourseElementRequest.topicId.ifPresent { topicId ->
            decreaseElementOrdersByTopicIdAndGreaterElementOrder(dao.topic?.id?.value, dao.order)
            dao.order = generateOrderByCourseAndTopicId(courseId, topicId)
            dao.topic = topicId?.let { CourseTopicDao.findById(it) }
        }

        return dao.toResponse(getElementDetailsByIdAndType(elementId, dao.type))
    }

    fun update(
        courseId: UUID,
        workId: UUID,
        request: UpdateCourseWorkRequest
    ): CourseWorkResponse? {
        val resultRow = CourseWorks.innerJoin(CourseElements, { CourseWorks.id }, { CourseElements.id })
            .select { CourseElements.courseId eq courseId and (CourseElements.id eq workId) }.singleOrNull()
        return resultRow?.let { row ->
            val elementDao = CourseElementDao.wrapRow(row)
            val workDao = CourseWorkDao.wrapRow(row)
            request.name.ifPresent { elementDao.name = it }
            request.description.ifPresent { elementDao.description = it }
            request.topicId.ifPresent { elementDao.topic = it?.let { id -> CourseTopicDao.findById(id) } }

            request.dueDate.ifPresent { workDao.dueDate = it }
            request.dueTime.ifPresent { workDao.dueTime = it }
            request.maxGrade.ifPresent { workDao.maxGrade = it }

            elementDao.toWorkResponse(workDao)
        }
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

fun generateOrderByCourseAndTopicId(courseId: UUID, topicId: UUID?) =
    CourseElementDao.getMaxOrderByCourseIdAndTopicId(courseId, topicId) + 1