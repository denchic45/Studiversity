package com.denchic45.studiversity.feature.user

import com.denchic45.studiversity.database.exists
import com.denchic45.studiversity.database.table.UserDao
import com.denchic45.studiversity.database.table.Users
import com.denchic45.studiversity.feature.auth.model.UserByEmail
import com.denchic45.studiversity.feature.role.repository.AddScopeRepoExt
import com.denchic45.stuiversity.api.account.model.UpdateAccountPersonalRequest
import com.denchic45.stuiversity.api.account.model.UpdatePasswordRequest
import com.denchic45.stuiversity.api.auth.AuthErrors
import com.denchic45.stuiversity.api.auth.model.SignupRequest
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.model.UpdateUserRequest
import com.denchic45.stuiversity.api.user.model.UserResponse
import io.ktor.client.*
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class UserRepository(
    private val client: HttpClient,
    private val avatarService: AvatarService
) : AddScopeRepoExt {

    fun add(signupRequest: SignupRequest): UserResponse {
        val hashed: String = BCrypt.hashpw(signupRequest.password, BCrypt.gensalt())
        return add(signupRequest.toCreateUser(), hashed)
    }

    fun add(request: CreateUserRequest, password: String): UserResponse {
        return UserDao.new {
            firstName = request.firstName
            surname = request.surname
            patronymic = request.patronymic
            email = request.email
            this.password = password
            avatarUrl = ""
            generatedAvatar = false
            gender = request.gender
        }.apply {
            avatarUrl = avatarService.generateAvatar(id.value)
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

    fun updatePersonal(userId: UUID, request: UpdateAccountPersonalRequest) {
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

    fun updateRecoveredPassword(userId: UUID, password: String) {
        UserDao[userId].password = BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun updatePassword(userId: UUID, request: UpdatePasswordRequest) {
        UserDao[userId].apply {
            if (!BCrypt.checkpw(request.oldPassword, password))
                throw BadRequestException(AuthErrors.INVALID_OLD_PASSWORD)
            password = BCrypt.hashpw(request.newPassword, BCrypt.gensalt())
        }
    }

    fun updateEmail(userId: UUID, email: String) {
        UserDao[userId].email = email
    }

    fun findUserByEmail(email: String): UserResponse? {
        return UserDao.find(Users.email eq email).singleOrNull()?.toUserResponse()
    }

    fun findEmailPasswordByEmail(email: String): UserByEmail? {
        return UserDao.find(Users.email eq email).singleOrNull()
            ?.let { UserByEmail(it.id.value, it.email, it.password) }
    }


    fun existByEmail(email: String): Boolean {
        return Users.exists { Users.email eq email }
    }


    fun find(query: String): List<UserResponse> = UserDao.find(
        Users.firstName.lowerCase() like "$query%"
                or (Users.surname.lowerCase() like "$query%")
                or (Users.patronymic.lowerCase() like "$query%")
    ).map(UserDao::toUserResponse)
}