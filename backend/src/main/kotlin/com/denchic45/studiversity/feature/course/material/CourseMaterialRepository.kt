package com.denchic45.studiversity.feature.course.material

import com.denchic45.studiversity.database.table.CourseDao
import com.denchic45.studiversity.database.table.CourseElementDao
import com.denchic45.studiversity.database.table.CourseElements
import com.denchic45.studiversity.database.table.CourseTopicDao
import com.denchic45.studiversity.feature.course.element.generateOrderByCourseAndTopicId
import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.stuiversity.api.course.material.model.CreateCourseMaterialRequest
import com.denchic45.stuiversity.api.course.material.model.UpdateCourseMaterialRequest
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import java.time.LocalDateTime
import java.util.*

class CourseMaterialRepository {
    fun add(courseId: UUID, request: CreateCourseMaterialRequest): UUID {
        val elementId = UUID.randomUUID()
        CourseElementDao.new(elementId) {
            this.course = CourseDao.findById(courseId) ?: throw NotFoundException()
            this.topic = request.topicId?.let { CourseTopicDao.findById(it) }
            this.name = request.name
            this.description = request.description
            this.type = CourseElementType.MATERIAL
            this.order = generateOrderByCourseAndTopicId(courseId, request.topicId)
        }
        return elementId
    }

    fun update(
        courseId: UUID,
        materialId: UUID,
        request: UpdateCourseMaterialRequest
    ): CourseMaterialResponse? {
        return CourseElementDao.find(
            CourseElements.courseId eq courseId and (CourseElements.id eq materialId)
        ).singleOrNull()?.also { dao ->
            dao.updatedAt = LocalDateTime.now()

            request.name.ifPresent { dao.name = it }
            request.description.ifPresent { dao.description = it }
            request.topicId.ifPresent {
                dao.topic = it?.let { id -> CourseTopicDao.findById(id) }
            }
        }?.toMaterialResponse()
    }

    fun findById(workId: UUID): CourseMaterialResponse? {
        return CourseElementDao.findById(workId)?.toMaterialResponse()
    }
}