package com.denchic45.studiversity.feature.course.element

import com.denchic45.studiversity.database.exists
import com.denchic45.studiversity.database.table.*
import com.denchic45.studiversity.feature.course.work.toWorkResponse
import com.denchic45.studiversity.util.toSqlSortOrder
import com.denchic45.stuiversity.api.course.element.model.*
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.javatime.CurrentDate
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

    private fun decreaseElementOrdersByTopicIdAndGreaterElementOrder(topicId: UUID?, order: Int) {
        CourseElements.update(where = { CourseElements.topicId eq topicId and (CourseElements.order greater order) }) {
            it[CourseElements.order] = CourseElements.order - 1
        }
    }

    private fun getElementDetailsByIdAndType(elementId: UUID, type: CourseElementType): CourseElementDetails {
        return when (type) {
            CourseElementType.WORK -> CourseWorkDao.findById(elementId)!!.toElementDetails()
            CourseElementType.MATERIAL -> CourseMaterial
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

    fun findByAuthor(authorId: UUID, late: Boolean?, submitted: Boolean?): List<CourseWorkResponse> {
        val query = CourseWorks.innerJoin(Submissions, { CourseWorks.id }, { courseWorkId })
            .select(Submissions.authorId eq authorId).orderBy(CourseWorks.dueDate)

        late?.let {
            query.andWhere {
                if (late)
                    CourseWorks.dueDate less CurrentDate
                else
                    CourseWorks.dueDate greater CurrentDate or (CourseWorks.dueDate eq null)
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
            CourseElementDao.findById(workDao.id.value)!!.toWorkResponse(workDao)
        }
    }
}

fun generateOrderByCourseAndTopicId(courseId: UUID, topicId: UUID?) =
    CourseElementDao.getMaxOrderByCourseIdAndTopicId(courseId, topicId) + 1