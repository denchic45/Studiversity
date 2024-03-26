package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp

object VerificationTokens : LongIdTable("verification_token") {
    val userId = reference("user_id", Users)
    val secret = uuid("secret")
    val createdAt = timestamp("created_at")
    val expireAt = timestamp("expire_at")
    val payload = text("payload")
}

