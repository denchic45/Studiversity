package com.studiversity.database.table

import com.studiversity.database.exists
import com.studiversity.database.type.timestampWithTimeZone
import com.studiversity.util.varcharMax
import com.stuiversity.api.course.element.model.CourseElementType
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import java.util.*

object CourseElements : UUIDTable("course_element", "course_element_id") {
    val courseId = uuid("course_id").references(Courses.id, onDelete = ReferenceOption.CASCADE,onUpdate = ReferenceOption.CASCADE)
    val topicId = uuid("topic_id").references(CourseTopics.id,onDelete = ReferenceOption.CASCADE,onUpdate = ReferenceOption.CASCADE).nullable()
    val name = varcharMax("element_name")
    val description = text("description").nullable()
    val order = integer("element_order")
    val createdAt = timestampWithTimeZone("created_at").defaultExpression(CurrentTimestamp())
    val updatedAt = timestampWithTimeZone("updated_at").nullable()
    val type = enumerationByName<CourseElementType>("element_type", 10)
}

class CourseElementDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CourseElementDao>(CourseElements) {
        fun existByCourseId(elementId: UUID, courseId: UUID): Boolean {
            return CourseElements.exists { CourseElements.id eq elementId and (CourseElements.courseId eq courseId) }
        }

        fun getMaxOrderByCourseIdAndTopicId(courseId: UUID, topicId: UUID?): Int {
            return CourseElements.slice(CourseElements.order.max())
                .select(
                    CourseElements.courseId eq courseId
                            and (CourseElements.topicId eq topicId)
                )
                .single().let { it[CourseElements.order.max()] ?: 0 }
        }
    }

    var courseId by CourseElements.courseId
    var topicId by CourseElements.topicId
    var name by CourseElements.name
    var description by CourseElements.description
    var order by CourseElements.order
    var createdAt by CourseElements.createdAt
    var updatedAt by CourseElements.updatedAt
    var type by CourseElements.type

    // todo add posts relation
}

sealed interface CourseElementDetailsDao