package com.studiversity.database.table

import com.stuiversity.api.course.work.model.CourseWorkType
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.time
import java.util.*

object CourseWorks : UUIDTable("course_work", "course_element_id") {
    val dueDate = date("due_date").nullable()
    val dueTime = time("due_time").nullable()
    val type = enumerationByName("work_type", 10, CourseWorkType::class)
    val maxGrade = short("max_grade")

    init {
        foreignKey(
            from = arrayOf(id),
            target = CourseElements.primaryKey,
            onUpdate = ReferenceOption.CASCADE,
            onDelete = ReferenceOption.CASCADE,
            name = "course_work_course_element_id_fk"
        )
    }
}

class CourseWorkDao(id: EntityID<UUID>) : UUIDEntity(id), CourseElementDetailsDao {
    companion object : UUIDEntityClass<CourseWorkDao>(CourseWorks)

    var dueDate by CourseWorks.dueDate
    var dueTime by CourseWorks.dueTime
    var type by CourseWorks.type
    var maxGrade by CourseWorks.maxGrade
}