package com.studiversity.feature.schedule

import com.denchic45.stuiversity.api.schedule.model.Schedule
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun Application.configureSchedule() {
    routing {
        authenticate("auth-jwt") {
            val json = Json { prettyPrint = true }
            route("/schedule") {
                get {
                    call.respond(
                        HttpStatusCode.OK,
                        json.decodeFromString<Schedule>(File("src/main/resources/schedule.json").readText())
                    )
                }
                put {
                    File("src/main/resources/schedule.json")
                        .writeText(json.encodeToString(call.receive<Schedule>()))
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}