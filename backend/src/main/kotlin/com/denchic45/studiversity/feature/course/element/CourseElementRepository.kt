package com.denchic45.studiversity.feature.course.element

import com.denchic45.studiversity.database.exists
import com.denchic45.studiversity.database.table.*
import com.denchic45.studiversity.feature.course.work.toWorkResponse
import com.denchic45.studiversity.util.toSqlSortOrder
import com.denchic45.stuiversity.api.course.element.CourseElementErrors
import com.denchic45.stuiversity.api.course.element.model.*
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.submission.model.SubmissionState
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import java.time.LocalDate
import java.util.*

class CourseElementRepository {

    fun findById(elementId: UUID): CourseElementResponse? {
        return CourseElementDao.findById(elementId)?.run {
            toResponse(getElementDetailsByIdAndType(elementId, type))
        }
    }

    fun findWorkById(workId: UUID): CourseWorkResponse? {
        return CourseWorkDao.findById(workId)
            ?.let { CourseElementDao.findById(workId)?.toWorkResponse(it) }
    }

    fun findCourseIdByElementId(elementId: UUID): UUID {
        return CourseElementDao[elementId].course.id.value
    }

    fun remove(elementId: UUID): Boolean = CourseElementDao
        .find(CourseElements.id eq elementId)
        .singleOrNull()?.apply {
            decreaseElementsOrdersByTopicIdAndGreaterOrder(topic?.id?.value, order)
        }?.delete() != null


    fun findTypeByElementId(elementId: UUID): CourseElementType? {
        return CourseElements.selectAll().where(CourseElements.id eq elementId)
            .singleOrNull()?.let { it[CourseElements.type] }
    }

    fun exist(courseId: UUID, elementId: UUID) = CourseElements.exists {
        CourseElements.id eq elementId and (CourseElements.courseId eq courseId)
    }

    fun update(
        elementId: UUID,
        request: UpdateCourseElementRequest
    ): CourseElementResponse {
        val courseElementDao = CourseElementDao[elementId]

        request.topicId.ifPresent { topicId ->
            if (topicId != null && CourseTopicDao.findById(topicId)?.courseId != courseElementDao.course.id.value)
                throw BadRequestException(CourseElementErrors.TOPIC_FROM_ANOTHER_COURSE)

            decreaseElementsOrdersByTopicIdAndGreaterOrder(courseElementDao.topic?.id?.value, courseElementDao.order)

            courseElementDao.order = generateOrderByCourseAndTopicId(findCourseIdByElementId(elementId), topicId)
            courseElementDao.topic = topicId?.let { CourseTopicDao.findById(it) }
        }

        return courseElementDao.toResponse(getElementDetailsByIdAndType(elementId, courseElementDao.type))
    }

    private fun decreaseElementsOrdersByTopicIdAndGreaterOrder(topicId: UUID?, order: Int) {
        CourseElements.update(where = {
            CourseElements.topicId eq topicId and (CourseElements.order greater order)
        }) {
            it[CourseElements.order] = CourseElements.order - 1
        }
    }

    private fun getElementDetailsByIdAndType(elementId: UUID, type: CourseElementType): CourseElementDetails {
        return when (type) {
            CourseElementType.WORK -> CourseWorkDao[elementId].toElementDetails()
            CourseElementType.MATERIAL -> CourseMaterial
        }
    }

    fun findElementsByCourseId(courseId: UUID, sorting: List<CourseElementsSorting>?): List<CourseElementResponse> {
        val query = CourseElements.selectAll().where(CourseElements.courseId eq courseId)
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

    fun findByAuthor(authorId: UUID, late: Boolean?, submitted: Boolean?): List<CourseWorkResponse> {
        val query = CourseWorks.innerJoin(Submissions, { CourseWorks.id }, { courseWorkId })
            .selectAll().where(Submissions.authorId eq authorId).orderBy(CourseWorks.dueDate)
        val currentDate = LocalDate.now()
        late?.let {
            query.andWhere {
                if (late) CourseWorks.dueDate lessEq currentDate
                else CourseWorks.dueDate greater currentDate or (CourseWorks.dueDate eq null)
            }
        }
        submitted?.let {
            query.andWhere {
                if (submitted)
                    Submissions.state notInList SubmissionState.notSubmitted()
                else
                    Submissions.state inList SubmissionState.notSubmitted()
            }
        }
        return CourseWorkDao.wrapRows(query).map { workDao ->
            CourseElementDao[workDao.id.value].toWorkResponse(workDao)
        }
    }

    fun isExists(elementId: UUID): Boolean {
        return CourseElements.exists { CourseElements.id eq elementId }
    }
}

fun generateOrderByCourseAndTopicId(courseId: UUID, topicId: UUID?): Int {
    return CourseElementDao.getMaxOrderByCourseIdAndTopicId(courseId, topicId) + 1
}