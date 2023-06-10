package com.denchic45.studiversity.feature.course.work

import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.studiversity.database.table.CourseDao
import com.denchic45.studiversity.database.table.CourseElementDao
import com.denchic45.studiversity.database.table.CourseTopicDao
import com.denchic45.studiversity.database.table.CourseWorkDao
import com.denchic45.studiversity.feature.course.element.generateOrderByCourseAndTopicId
import com.denchic45.studiversity.feature.course.element.toResponse
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class CourseWorkRepository {
    fun addWork(courseId: UUID, request: CreateCourseWorkRequest):UUID {
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

    fun findWorkById(workId:UUID): CourseWorkResponse {
        return CourseElementDao.findById(workId)!!.toWorkResponse(CourseWorkDao.findById(workId)!!)
    }
}