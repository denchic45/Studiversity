package com.studiversity.database.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

object Lessons : IdTable<Long>("lesson") {
    override val id: Column<EntityID<Long>> = long("period_id").autoIncrement().entityId()
        .references(Periods.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(id)
    val courseId = uuid("course_id").references(
        Courses.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
}

class LessonDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<LessonDao>(Lessons)

    var periodId by Periods.id
    var courseId by Lessons.courseId

    var period by PeriodDao referencedOn Lessons.id
    var course by CourseDao referencedOn Courses.id
}