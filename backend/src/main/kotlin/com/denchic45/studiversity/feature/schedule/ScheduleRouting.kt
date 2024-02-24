package com.denchic45.studiversity.feature.schedule

import com.denchic45.stuiversity.api.schedule.model.Period
import com.denchic45.stuiversity.api.schedule.model.Schedule
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Application.configureSchedule() {
    routing {
        authenticate("auth-jwt") {
//            val bucket: BucketApi by inject()
            val json = Json { prettyPrint = true }
            route("/schedule") {
                get {
//                    val schedule = bucket.downloadPublic("schedule.json").decodeToString()
                    val schedule = Schedule(
                        periods = listOf(
                            Period("08:30", "09:30"),
                            Period("09:40", "11:10"),
                            Period("11:40", "13:10"),
                            Period("13:20", "14:50"),
                            Period("15:00", "16:30")
                        ), lunch = null
                    )
                    call.respond(schedule)
                }
                put {
                    // TODO: восстановить
//                    val encodeToString = json.encodeToString(call.receive<Schedule>())
//                    println("json: $encodeToString")
//                    val data = encodeToString.toByteArray()
//                    println("data: $data")
//                    bucket.update("schedule.json", data)
//                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}
