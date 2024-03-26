package com.denchic45.studiversity.database.table

import com.denchic45.studiversity.feature.user.account.model.TokenAction
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp

object VerificationTokens : UUIDTable("verification_token") {
    val secret = text("secret")
    val userId = reference("user_id", Users)
    val action = enumerationByName<TokenAction>("action", 16)
    val createdAt = timestamp("created_ate")
    val expireAt = timestamp("expire_ate")
    val payload = text("payload")
}

