package com.denchic45.studiversity.database.table

import com.denchic45.studiversity.database.type.timestampWithTimeZone
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object MagicLinks : LongIdTable("magic_link") {
    val token = text("token")
    val userId = reference("user_id", Users)
    val expireAt = timestampWithTimeZone("expire_at")
}

object ConfirmCodes : LongIdTable("confirm_code") {
    val code = text("code")
    val userId = reference("user_id", Users)
    val createdAt = timestamp("created_at")
    val expireAt = timestamp("expire_at")
}

class ConfirmCodeDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ConfirmCodeDao>(ConfirmCodes)

    var code by ConfirmCodes.code
    var createdAt by ConfirmCodes.createdAt
    var expireAt by ConfirmCodes.expireAt

    var user by UserDao referencedOn ConfirmCodes.userId
}