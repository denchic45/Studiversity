package com.studiversity.database.table

import com.denchic45.stuiversity.api.timetable.model.PeriodType
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.date

object Periods : LongIdTable("period", "period_id") {
    val date = date("date")
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
    val type = enumerationByName<PeriodType>("period_type", 10)
}

class PeriodDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PeriodDao>(Periods)

    var date by Periods.date
    var order by Periods.order

    //    var roomId by Periods.roomId
//    var studyGroupId by Periods.studyGroupId
    var type by Periods.type

    var room by RoomDao optionalReferencedOn Periods.roomId
    var studyGroup by StudyGroupDao referencedOn Periods.studyGroupId

    val members by PeriodMemberDao referrersOn PeriodsMembers.periodId

    val lesson by LessonDao backReferencedOn Lessons.id
    val event by EventDao backReferencedOn Events.id
}