package com.denchic45.studiversity.feature.user

import com.denchic45.studiversity.database.exists
import com.denchic45.studiversity.database.table.MagicLinks
import com.denchic45.studiversity.database.table.RefreshTokens
import com.denchic45.studiversity.database.table.UserDao
import com.denchic45.studiversity.database.table.Users
import com.denchic45.studiversity.feature.auth.model.MagicLinkToken
import com.denchic45.studiversity.feature.auth.model.RefreshToken
import com.denchic45.studiversity.feature.auth.model.UserByEmail
import com.denchic45.studiversity.feature.role.repository.AddScopeRepoExt
import com.denchic45.stuiversity.api.account.model.UpdateAccountPersonalRequest
import com.denchic45.stuiversity.api.account.model.UpdateEmailRequest
import com.denchic45.stuiversity.api.account.model.UpdatePasswordRequest
import com.denchic45.stuiversity.api.auth.AuthErrors
import com.denchic45.stuiversity.api.auth.model.SignupRequest
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.model.UpdateUserRequest
import com.denchic45.stuiversity.api.user.model.UserResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant
import java.util.*

class UserRepository(private val client: HttpClient) : AddScopeRepoExt {

    suspend fun add(signupRequest: SignupRequest): UserResponse {
        val hashed: String = BCrypt.hashpw(signupRequest.password, BCrypt.gensalt())
        return add(signupRequest.toCreateUser(), hashed)
    }

    suspend fun add(user: CreateUserRequest, password: String): UserResponse {
        return UserDao.new {
            firstName = user.firstName
            surname = user.surname
            patronymic = user.patronymic
            email = user.email
            this.password = password
            avatarUrl = ""
            generatedAvatar = false
            gender = user.gender
        }.apply {
            avatarUrl = generateAvatar(id.value)
        }.toUserResponse()
//            .apply { addScope(id, ScopeType.User, organizationId) }
    }

    fun findById(id: UUID): UserResponse? {
        return UserDao.findById(id)?.toUserResponse()
    }

    fun remove(userId: UUID): Boolean {
        return Users.deleteWhere { Users.id eq userId } == 1
    }

    fun updateUser(userId: UUID, request: UpdateUserRequest) {
        UserDao[userId].apply {
            request.firstName.ifPresent {
                firstName = it
            }
            request.surname.ifPresent {
                surname = it
            }
            request.patronymic.ifPresent {
                patronymic = it
            }
        }
    }

    fun updateAccount(userId: UUID, request: UpdateAccountPersonalRequest) {
        UserDao[userId].apply {
            request.firstName.ifPresent {
                firstName = it
            }
            request.surname.ifPresent {
                surname = it
            }
            request.patronymic.ifPresent {
                patronymic = it
            }
        }
    }

    private suspend fun setAvatar(userId: UUID, request: CreateFileRequest, generated: Boolean): String {
//        val newPath = "avatars/$userId.${File(request.name).extension}"
//        bucket.upload(newPath, request.bytes)
//        return bucket.publicUrl(newPath).also {
//            UserDao.findById(userId)!!.apply {
//                avatarUrl = it
//                generatedAvatar = generated
//            }
//        }
        return ""
    }

    suspend fun updateAvatar(userId: UUID, request: CreateFileRequest) {
        deleteAvatar(userId)
        setAvatar(userId, request, false)
    }

    suspend fun resetAvatar(userId: UUID) {
        deleteAvatar(userId)
        generateAvatar(userId)
    }

    private suspend fun deleteAvatar(userId: UUID) {
//        val name = bucket.list(prefix = "avatars") { search = userId.toString() }.single().name
//        bucket.delete("avatars/$name")
    }

    private suspend fun generateAvatar(userId: UUID): String {
        val newImageBytes = client.get("https://ui-avatars.com/api") {
            parameter("name", UserDao.findById(userId)!!.firstName[0])
            parameter("background", "random")
            parameter("format", "png")
            parameter("size", 128)
        }.readBytes()

        return setAvatar(userId, CreateFileRequest("avatar.png", newImageBytes), true)
    }

    fun updateAccount(userId: UUID, updatePasswordRequest: UpdatePasswordRequest) {
        UserDao.findById(userId)!!.apply {
            if (!BCrypt.checkpw(updatePasswordRequest.oldPassword, password))
                throw BadRequestException(AuthErrors.INVALID_PASSWORD)
            password = BCrypt.hashpw(updatePasswordRequest.newPassword, BCrypt.gensalt())
        }
    }

    fun updateAccount(userId: UUID, updateEmailRequest: UpdateEmailRequest) {
        UserDao.findById(userId)!!.email = updateEmailRequest.email
    }

    fun findUserByEmail(email: String): UserResponse? {
        return UserDao.find(Users.email eq email).singleOrNull()?.toUserResponse()
    }

    fun findEmailPasswordByEmail(email: String): UserByEmail? {
        return UserDao.find(Users.email eq email).singleOrNull()
            ?.let { UserByEmail(it.id.value, it.email, it.password) }
    }

    fun addToken(createRefreshToken: RefreshToken): String {
        return RefreshTokens.insert {
            it[userId] = createRefreshToken.userId
            it[token] = createRefreshToken.token
            it[expireAt] = createRefreshToken.expireAt
        }[RefreshTokens.token]
    }

    fun existByEmail(email: String): Boolean {
        return Users.exists { Users.email eq email }
    }

    fun findRefreshToken(refreshToken: String): RefreshToken? {
        val expiredTokens = RefreshTokens.select(RefreshTokens.expireAt less Instant.now()).limit(100)
            .map { it[RefreshTokens.id] }
        RefreshTokens.deleteWhere { RefreshTokens.id inList expiredTokens }
        return RefreshTokens.select(RefreshTokens.token eq refreshToken)
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

    fun addMagicLink(magicLinkToken: MagicLinkToken): String {
        return MagicLinks.insert {
            it[token] = magicLinkToken.token
            it[userId] = magicLinkToken.userId
            it[expireAt] = magicLinkToken.expireAt
        }[MagicLinks.token]
    }

    fun removeMagicLink(token: String) {
        MagicLinks.deleteWhere { this.token eq token }
    }

    fun findMagicLinkByToken(token: String): MagicLinkToken? {
        val expiredTokens = MagicLinks.select(MagicLinks.expireAt less Instant.now()).limit(100)
            .map { it[MagicLinks.id] }
        MagicLinks.deleteWhere { MagicLinks.id inList expiredTokens }

        return MagicLinks.select(MagicLinks.token eq token).singleOrNull()?.let {
            MagicLinkToken(
                userId = it[MagicLinks.userId].value,
                token = it[MagicLinks.token],
                expireAt = it[MagicLinks.expireAt]
            )
        }
    }

    fun find(query: String): List<UserResponse> = UserDao.find(
        Users.firstName.lowerCase() like "$query%"
                or (Users.surname.lowerCase() like "$query%")
                or (Users.patronymic.lowerCase() like "$query%")
    ).map(UserDao::toUserResponse)
}