package com.denchic45.studiversity.feature.room

import com.denchic45.studiversity.config
import com.denchic45.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.denchic45.studiversity.feature.room.usecase.AddRoomUseCase
import com.denchic45.studiversity.feature.room.usecase.FindRoomByIdUseCase
import com.denchic45.studiversity.feature.room.usecase.RemoveRoomUseCase
import com.denchic45.studiversity.feature.room.usecase.UpdateRoomUseCase
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.stuiversity.api.role.model.Capability
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRooms() {
    routing {
        authenticate("auth-jwt") {
            route("/rooms") {
                val requireCapability: RequireCapabilityUseCase by inject()
                val addRoom: AddRoomUseCase by inject()

                post {
                    requireCapability(call.currentUserId(), Capability.WriteRoom, config.organization.id)
                    call.respond(HttpStatusCode.Created, addRoom(call.receive()))
                }
                route("/{roomId}") {
                    val findRoomById: FindRoomByIdUseCase by inject()
                    val updateRoom: UpdateRoomUseCase by inject()
                    val removeRoom: RemoveRoomUseCase by inject()

                    get {
                        call.respond(HttpStatusCode.OK, findRoomById(call.parameters.getUuidOrFail("roomId")))
                    }
                    patch {
                        requireCapability(call.currentUserId(), Capability.WriteRoom, config.organization.id)
                        val room = updateRoom(call.parameters.getUuidOrFail("roomId"), call.receive())
                        call.respond(HttpStatusCode.OK, room)
                    }
                    delete {
                        requireCapability(
                            call.currentUserId(),
                            Capability.WriteRoom,
                            config.organization.id
                        )
                        removeRoom(call.parameters.getUuidOrFail("roomId"))
                        call.respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}