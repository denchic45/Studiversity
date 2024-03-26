package com.denchic45.studiversity.feature.user

import com.denchic45.studiversity.database.table.*
import com.denchic45.studiversity.feature.auth.model.RefreshToken
import com.denchic45.studiversity.feature.user.account.model.ConfirmationCode
import com.denchic45.studiversity.feature.user.account.model.VerificationToken
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.time.Instant
import java.util.*
import kotlin.random.Random

class TokenRepository {

    fun addRefreshToken(refreshToken: RefreshToken): String {
        return RefreshTokens.insert {
            it[userId] = refreshToken.userId
            it[token] = refreshToken.token
            it[expireAt] = refreshToken.expireAt
        }[RefreshTokens.token]
    }

    fun findRefreshToken(refreshToken: String): RefreshToken? {
        val expiredTokens =
            RefreshTokens.selectAll().where(RefreshTokens.expireAt less Instant.now()).limit(100)
                .map { it[RefreshTokens.id] }
        RefreshTokens.deleteWhere { RefreshTokens.id inList expiredTokens }
        return RefreshTokens.selectAll().where(RefreshTokens.token eq refreshToken)
            .singleOrNull()?.let {
                RefreshToken(
                    it[RefreshTokens.userId].value,
                    it[RefreshTokens.token],
                    it[RefreshTokens.expireAt]
                )
            }
    }


    fun removeRefreshToken(refreshToken: String) {
        RefreshTokens.deleteWhere { token eq refreshToken }
    }

//    fun addMagicLink(magicLinkToken: MagicLinkToken): String {
//        return MagicLinks.insert {
//            it[token] = magicLinkToken.token
//            it[userId] = magicLinkToken.userId
//            it[expireAt] = magicLinkToken.expireAt
//        }[MagicLinks.token]
//    }

//    fun removeMagicLink(token: String) {
//        MagicLinks.deleteWhere { this.token eq token }
//    }

//    fun findMagicLinkByToken(token: String): MagicLinkToken? {
//        val expiredTokens = MagicLinks.selectAll().where(MagicLinks.expireAt less Instant.now()).limit(100)
//            .map { it[MagicLinks.id] }
//        MagicLinks.deleteWhere { MagicLinks.id inList expiredTokens }
//
//        return MagicLinks.selectAll().where(MagicLinks.token eq token).singleOrNull()?.let {
//            MagicLinkToken(
//                userId = it[MagicLinks.userId].value,
//                token = it[MagicLinks.token],
//                expireAt = it[MagicLinks.expireAt]
//            )
//        }
//    }

    fun generateVerificationToken(userId: UUID, payload: String, secondsDuration: Long): UUID {
        return VerificationTokens.insert {
            it[VerificationTokens.userId] = userId
            it[secret] = UUID.randomUUID()
            val now = Instant.now()
            it[createdAt] = now
            it[expireAt] = now.plusSeconds(secondsDuration)
            it[VerificationTokens.payload] = payload
        }[VerificationTokens.secret]
    }

    fun findVerificationToken(token: UUID): VerificationToken? {
        return VerificationTokens.selectAll().where(VerificationTokens.secret eq token)
            .singleOrNull()?.let {
                VerificationToken(
                    secret = token,
                    userId = it[VerificationTokens.userId].value,
                    payload = it[VerificationTokens.payload],
                    expired = it[VerificationTokens.expireAt].isBefore(Instant.now())
                )
            }
    }

    fun removeVerificationToken(token: UUID) {
        VerificationTokens.deleteWhere { secret eq token }
    }

    fun generateCode(userId: UUID, secondsDuration: Long): ConfirmationCode {
        val dao = ConfirmCodeDao.new {
            code = List(10) { Random.nextInt(0, 100) }.joinToString(separator = "")
            user = UserDao[userId]
            val now = Instant.now()
            createdAt = now
            expireAt = now.plusSeconds(secondsDuration)
        }

        return dao.toResponse()
    }

    fun findConfirmCode(code: String): ConfirmationCode? {
        return ConfirmCodeDao.find(ConfirmCodes.code eq code).singleOrNull()?.toResponse()
    }

    fun ConfirmCodeDao.toResponse() = ConfirmationCode(
        code = code,
        userId = user.id.value,
        expired = expireAt.isBefore(Instant.now())
    )

    fun removeConfirmCode(code: String) {
        ConfirmCodes.deleteWhere { ConfirmCodes.code eq code }
    }
}