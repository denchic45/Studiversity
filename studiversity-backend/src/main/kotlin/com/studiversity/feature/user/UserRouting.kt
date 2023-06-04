package com.studiversity.feature.user

import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.role.model.Capability
import com.studiversity.config
import com.studiversity.feature.auth.usecase.SignUpUserManuallyUseCase
import com.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.studiversity.feature.user.account.usecase.ResetAvatarUseCase
import com.studiversity.feature.user.account.usecase.UpdateAvatarUseCase
import com.studiversity.feature.user.usecase.FindUserByIdUseCase
import com.studiversity.feature.user.usecase.RemoveUserUseCase
import com.studiversity.feature.user.usecase.SearchUsersUseCase
import com.studiversity.ktor.CommonErrors
import com.studiversity.ktor.currentUserId
import com.studiversity.ktor.getUuidOrFail
import com.studiversity.util.tryToUUID
import com.studiversity.validation.require
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Application.userRoutes() {
    routing {
        authenticate("auth-jwt") {
            route("/users") {
                val requireCapability: RequireCapabilityUseCase by inject()
                val signUpUserManually: SignUpUserManuallyUseCase by inject()
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
                        scopeId = config.organization.id
                    )
                    call.respond(HttpStatusCode.Created, signUpUserManually(call.receive()))
                }
                userByIdRoute()
            }
        }
    }
}

private fun Route.userByIdRoute() {
    route("/{userId}") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findUserById: FindUserByIdUseCase by inject()
        val removeUser: RemoveUserUseCase by inject()

        get {
            val currentUserId = call.currentUserId()
            val userId = when (val parameter = call.parameters.getOrFail("userId")) {
                "me" -> currentUserId
                else -> parameter.tryToUUID()
            }
            val user = findUserById(userId)
            call.respond(HttpStatusCode.OK, user)
        }

        delete {
            requireCapability(
                userId = call.currentUserId(),
                capability = Capability.DeleteUser,
                scopeId = config.organization.id
            )

            removeUser(call.parameters.getUuidOrFail("userId"))
            call.respond(HttpStatusCode.NoContent)
        }
        route("/avatar") {
            val updateAvatar: UpdateAvatarUseCase by inject()
            val removeAvatar: ResetAvatarUseCase by inject()

            put {
                val photoRequest = call.receiveMultipart().readPart()?.let { part ->
                    if (part is PartData.FileItem) {
                        val fileSourceName = part.originalFileName as String
                        val fileBytes = part.streamProvider().readBytes()
                        CreateFileRequest(fileSourceName, fileBytes)
                    } else throw BadRequestException("INVALID_AVATAR")
                } ?: throw BadRequestException("INVALID_AVATAR")

                val url = updateAvatar(
                    userId = call.parameters.getUuidOrFail("userId"),
                    request = photoRequest
                )
                call.respond(url)
            }

            delete {
                val url = removeAvatar(call.parameters.getUuidOrFail("userId"))
                call.respond(url)
            }
        }
    }
}


