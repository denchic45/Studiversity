package com.denchic45.studiversity.feature.schedule

import com.denchic45.stuiversity.api.schedule.model.Period
import com.denchic45.stuiversity.api.schedule.model.Schedule
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSchedule() {
    routing {
        authenticate("auth-jwt") {
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
