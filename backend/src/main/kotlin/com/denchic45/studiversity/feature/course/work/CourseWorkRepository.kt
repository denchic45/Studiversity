package com.denchic45.studiversity.feature.course.work

import com.denchic45.studiversity.database.table.*
import com.denchic45.studiversity.feature.course.element.generateOrderByCourseAndTopicId
import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDateTime
import java.util.*

class CourseWorkRepository {
    fun add(courseId: UUID, request: CreateCourseWorkRequest): UUID {
        val elementId = UUID.randomUUID()
        CourseElementDao.new(elementId) {
            this.course = CourseDao.findById(courseId) ?: throw NotFoundException()
            this.topic = request.topicId?.let { CourseTopicDao.findById(it) }
            this.name = request.name
            this.description = request.description
            this.type = CourseElementType.WORK
            this.order = generateOrderByCourseAndTopicId(courseId, request.topicId)
        }
        CourseWorkDao.new(elementId) {
            this.dueDate = request.dueDate
            this.dueTime = request.dueTime
            this.type = request.workType
            this.maxGrade = request.maxGrade
        }
        return elementId
    }

    fun update(workId: UUID, request: UpdateCourseWorkRequest): CourseWorkResponse? {
        val elementDao = CourseWorks.innerJoin(CourseElements, { CourseWorks.id }, { CourseElements.id })
            .selectAll().where { CourseElements.id eq workId }.singleOrNull()
            ?.let(CourseElementDao.Companion::wrapRow)

        return elementDao?.let {
            elementDao.updatedAt = LocalDateTime.now()

            val workDao = CourseWorkDao[elementDao.id]
            request.name.ifPresent { elementDao.name = it }
            request.description.ifPresent { elementDao.description = it }

            request.dueDate.ifPresent { workDao.dueDate = it }
            request.dueTime.ifPresent { workDao.dueTime = it }
            request.maxGrade.ifPresent { workDao.maxGrade = it }

            elementDao.toWorkResponse(workDao)
        }
    }

    fun findWorkById(workId: UUID): CourseWorkResponse {
        return CourseElementDao.findById(workId)!!.toWorkResponse(CourseWorkDao.findById(workId)!!)
    }
}