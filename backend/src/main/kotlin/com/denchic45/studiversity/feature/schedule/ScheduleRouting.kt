package com.denchic45.studiversity.feature.schedule

import com.denchic45.stuiversity.api.schedule.model.Schedule
import io.github.jan.supabase.storage.BucketApi
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

fun Application.configureSchedule() {
    routing {
        authenticate("auth-jwt") {
            val bucket: BucketApi by inject()
            val json = Json { prettyPrint = true }
            route("/schedule") {
                get {
                    val schedule = bucket.downloadPublic("schedule.json").decodeToString()
                    call.respond(
                        HttpStatusCode.OK,
                        json.decodeFromString<Schedule>(schedule)
                    )
                }
                put {
                    val encodeToString = json.encodeToString(call.receive<Schedule>())
                    println("json: $encodeToString")
                    val data = encodeToString.toByteArray()
                    println("data: $data")
                    bucket.update("schedule.json", data)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}
