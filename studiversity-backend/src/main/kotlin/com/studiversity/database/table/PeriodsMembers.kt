package com.studiversity.database.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object PeriodsMembers : LongIdTable("period_member", "period_member_id") {
    val periodId = reference("period_id", Periods, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val memberId = reference("member_id", Users, ReferenceOption.CASCADE, ReferenceOption.CASCADE)

    init {
        uniqueIndex("period_member_un", periodId, memberId)
    }
}

class PeriodMemberDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PeriodMemberDao>(PeriodsMembers)

    var period by PeriodDao referencedOn PeriodsMembers.periodId
    var member by UserDao referencedOn PeriodsMembers.memberId
}