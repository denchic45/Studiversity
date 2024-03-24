package com.denchic45.studiversity.feature.user

import com.denchic45.studiversity.config.config
import com.denchic45.studiversity.database.table.UserDao
import com.denchic45.studiversity.feature.auth.usecase.AddUserUseCase
import com.denchic45.studiversity.feature.auth.usecase.UpdateUserUseCase
import com.denchic45.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.denchic45.studiversity.feature.user.account.usecase.ResetAvatarUseCase
import com.denchic45.studiversity.feature.user.account.usecase.UpdateAvatarUseCase
import com.denchic45.studiversity.feature.user.usecase.FindUserAvatarUseCase
import com.denchic45.studiversity.feature.user.usecase.FindUserByIdUseCase
import com.denchic45.studiversity.feature.user.usecase.RemoveUserUseCase
import com.denchic45.studiversity.feature.user.usecase.SearchUsersUseCase
import com.denchic45.studiversity.ktor.CommonErrors
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getUserUuidByParameterOrMe
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.studiversity.validation.require
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.user.AvatarErrors
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject
import org.mindrot.jbcrypt.BCrypt

fun Application.userRoutes() {
    routing {
        authenticate("auth-jwt") {
            route("/users") {
                val requireCapability: RequireCapabilityUseCase by inject()
                val addUser: AddUserUseCase by inject()
                val searchUsers: SearchUsersUseCase by inject()

                get {
                    val q = call.request.queryParameters.getOrFail("q").require(
                        String::isNotBlank,
                        CommonErrors::PARAMETER_MUST_NOT_BE_EMPTY
                    )
                    call.respond(HttpStatusCode.OK, searchUsers(q))
                }

                post {
                    requireCapability(
                        userId = call.currentUserId(),
                        capability = Capability.WriteUser,
                        scopeId = config.organizationId
                    )
                    call.respond(HttpStatusCode.Created, addUser(call.receive()))
                }
                userByIdRoute()
            }
            avatarRoute()
        }
    }
}

private fun Route.userByIdRoute() {
    route("/{userId}") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findUserById: FindUserByIdUseCase by inject()
        val updateUser: UpdateUserUseCase by inject()
        val removeUser: RemoveUserUseCase by inject()

        get {
            val userId = call.getUserUuidByParameterOrMe("userId")
            val user = findUserById(userId)
            call.respond(HttpStatusCode.OK, user)
        }

        put {
            requireCapability(
                userId = call.currentUserId(),
                capability = Capability.WriteUser,
                scopeId = config.organizationId
            )
            val userId = call.getUserUuidByParameterOrMe("userId")
            updateUser(userId, call.receive())
        }

        delete {
            requireCapability(
                userId = call.currentUserId(),
                capability = Capability.DeleteUser,
                scopeId = config.organizationId
            )

            removeUser(call.parameters.getUuidOrFail("userId"))
            call.respond(HttpStatusCode.NoContent)
        }

        // TODO Удалить потом!
        post("update-password") {
            val userId = call.parameters.getUuidOrFail("userId")
            val newPassword = call.request.queryParameters["p"]
            transaction {
                UserDao.findById(userId)!!.apply {
                    password = BCrypt.hashpw(newPassword, BCrypt.gensalt())
                }
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}

private fun Route.avatarRoute() {
    route("users/{userId}/avatar") {
        val updateAvatar: UpdateAvatarUseCase by inject()
        val resetAvatar: ResetAvatarUseCase by inject()

        put {
            call.receiveMultipart().readPart()?.let { part ->
                if (part is PartData.FileItem) {
                    updateAvatar(
                        userId = call.parameters.getUuidOrFail("userId"),
                        inputStream = part.streamProvider(),
                        extension = part.originalFileName?.substringAfterLast('.', "")
                            ?: throw BadRequestException(AvatarErrors.INVALID_FILE_NAME)
                    )
                    call.respond(HttpStatusCode.OK)
                } else throw BadRequestException(AvatarErrors.INVALID_AVATAR)
            } ?: throw BadRequestException(AvatarErrors.INVALID_AVATAR)
        }

        delete {
            val url = resetAvatar(call.parameters.getUuidOrFail("userId"))
            call.respond(url)
        }
    }

    route("/avatars") {
        val findUserAvatar: FindUserAvatarUseCase by inject()
        get("/{userId}") {
            val userId = call.parameters.getUuidOrFail("userId")
            val avatar = findUserAvatar(userId)
            call.respondBytes(
                contentType = ContentType.defaultForFileExtension(avatar.name),
                provider = { avatar.byteArray }
            )
        }
    }
}