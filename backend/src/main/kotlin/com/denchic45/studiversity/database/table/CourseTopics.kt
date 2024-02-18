package com.denchic45.studiversity.database.table


import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import java.util.*

object CourseTopics : UUIDTable("course_topic", "course_topic_id") {
    val courseId = uuid("course_id").references(Courses.id)
    val name = text("topic_name")
    val order = integer("topic_order")
}

class CourseTopicDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CourseTopicDao>(CourseTopics) {
        fun getMaxOrderByCourseId(courseId: UUID): Int {
            return CourseTopics.slice(CourseTopics.order.max())
                .select(CourseTopics.courseId eq courseId)
                .single().let { it[CourseTopics.order.max()] ?: 0 }
        }
    }

    var courseId by CourseTopics.courseId
    var name by CourseTopics.name
    var order by CourseTopics.order
}