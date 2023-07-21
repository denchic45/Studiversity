package com.denchic45.studiversity.feature.course.work

import com.denchic45.studiversity.database.table.*
import com.denchic45.studiversity.feature.course.element.generateOrderByCourseAndTopicId
import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.select
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

    fun update(
        courseId: UUID,
        workId: UUID,
        request: UpdateCourseWorkRequest
    ): CourseWorkResponse? {
        val resultRow = CourseWorks.innerJoin(CourseElements, { CourseWorks.id }, { CourseElements.id })
            .select { CourseElements.courseId eq courseId and (CourseElements.id eq workId) }.singleOrNull()
        return resultRow?.let { row ->
            val elementDao = CourseElementDao.wrapRow(row)

            elementDao.updatedAt = LocalDateTime.now()

            val workDao = CourseWorkDao.wrapRow(row)
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