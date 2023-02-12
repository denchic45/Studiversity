package com.studiversity.database.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.CustomFunction
import org.jetbrains.exposed.sql.javatime.JavaInstantColumnType
import org.jetbrains.exposed.sql.javatime.timestamp

object UsersMemberships : LongIdTable("user_membership", "user_membership_id") {
    val membershipId = reference("membership_id", Memberships.id)
    val memberId = uuid("member_id").references(Users.id)
    val joinAt = timestamp("join_at").defaultExpression(CustomFunction("now", JavaInstantColumnType()))

    init {
        uniqueIndex("user_membership_un", membershipId, memberId)
    }
}