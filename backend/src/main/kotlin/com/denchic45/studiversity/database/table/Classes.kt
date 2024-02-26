package com.denchic45.studiversity.database.table

import com.denchic45.stuiversity.api.timetable.model2.ClassRecurrenceType
import com.denchic45.stuiversity.api.timetable.model2.ClassType
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CustomDateFunction
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.stringLiteral

object Classes : LongIdTable("period", "period_id") {
    val date = text("date")
    val recurrence = enumerationByName<ClassRecurrenceType>("recurrence", 16)
    val startAt = date("start_at").nullable()
    val endAt = date("end_at").nullable()
    val order = integer("period_order")
    val roomId = optReference(
        "room_id", Rooms,
        onDelete = ReferenceOption.SET_NULL,
        onUpdate = ReferenceOption.SET_NULL
    )
    val studyGroupId = reference(
        "study_group_id", StudyGroups,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val courseId = reference(
        "course_id", Courses.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val type = enumerationByName<ClassType>("class_type", 10)
}

class ClassDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ClassDao>(Classes)

    var date by Classes.date
    var order by Classes.order
    var type by Classes.type

    var room by RoomDao optionalReferencedOn Classes.roomId
    var studyGroup by StudyGroupDao referencedOn Classes.studyGroupId

//    val members by ClassMemberDao referrersOn ClassesMembers.classId
}



fun Expression<String>.toDate(format: String = "YYYY-MM-DD") =
    CustomDateFunction("TO_DATE", this, stringLiteral(format)).date()