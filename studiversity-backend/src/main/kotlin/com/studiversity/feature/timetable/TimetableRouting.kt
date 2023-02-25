package com.studiversity.feature.timetable

import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.timetable.model.PeriodsSorting
import com.denchic45.stuiversity.util.toUUID
import com.studiversity.config
import com.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.studiversity.feature.timetable.usecase.*
import com.studiversity.ktor.currentUserId
import com.studiversity.ktor.getSortingBy
import com.studiversity.ktor.getUuid
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Application.timetableRoutes() {
    routing {
        authenticate("auth-jwt") {
            // TODO validate requests
            route("/timetables/{weekOfYear}") {
                val putTimetable: PutTimetableUseCase by inject()
                val findTimetableByStudyGroup: FindTimetableUseCase by inject()
                val removeTimetable: RemoveTimetableUseCase by inject()
                val requireCapability: RequireCapabilityUseCase by inject()
                put {
                    requireCapability(call.currentUserId(), Capability.WriteTimetable, config.organization.id)

                    val weekOfYear = call.parameters.getOrFail("weekOfYear")
                    val timetable = putTimetable(weekOfYear, call.receive())
                    call.respond(HttpStatusCode.OK, timetable)
                }
                get {
                    val weekOfYear = call.parameters.getOrFail("weekOfYear")

                    val studyGroupIds = call.request.queryParameters.getAll("studyGroupId")?.map(String::toUUID)
                    val courseIds = call.request.queryParameters.getAll("courseId")?.map(String::toUUID)
                    val memberIds = call.request.queryParameters.getAll("memberId")?.map(String::toUUID)
                    val roomIds = call.request.queryParameters.getAll("roomId")?.map(String::toUUID)

                    if (studyGroupIds == null && courseIds == null && memberIds == null && roomIds == null)
                        throw MissingRequestParameterException("period field")

                    val timetable = findTimetableByStudyGroup(
                        studyGroupIds = studyGroupIds,
                        courseIds = courseIds,
                        memberIds = memberIds,
                        roomIds = roomIds,
                        weekOfYear = weekOfYear,
                        sorting = call.request.queryParameters.getSortingBy(PeriodsSorting)
                    )
                    call.respond(HttpStatusCode.OK, timetable)
                }
                delete {
                    requireCapability(call.currentUserId(), Capability.WriteTimetable, config.organization.id)
                    val weekOfYear = call.parameters.getOrFail("weekOfYear")
                    removeTimetable(call.request.queryParameters.getUuid("studyGroupId"), weekOfYear)
                    call.respond(HttpStatusCode.NoContent)
                }
                route("/{dayOfWeek}") {
                    val putTimetableOfDay: PutTimetableOfDayUseCase by inject()
                    val findTimetableOfDay: FindTimetableOfDayUseCase by inject()
                    val removeTimetableOfDay: RemoveTimetableOfDayUseCase by inject()
                    put {
                        requireCapability(call.currentUserId(), Capability.WriteTimetable, config.organization.id)

                        val weekOfYear = call.parameters.getOrFail("weekOfYear")
                        val dayOfWeek = call.parameters.getOrFail("dayOfWeek").toInt()
                        val timetableOfDay = putTimetableOfDay(weekOfYear, dayOfWeek, call.receive())
                        call.respond(HttpStatusCode.OK, timetableOfDay)
                    }
                    get {
                        val weekOfYear = call.parameters.getOrFail("weekOfYear")
                        val dayOfWeek = call.parameters.getOrFail("dayOfWeek").toInt()
                        val studyGroupIds = call.request.queryParameters.getAll("studyGroupId")?.map(String::toUUID)
                        val courseIds = call.request.queryParameters.getAll("courseId")?.map(String::toUUID)
                        val memberIds = call.request.queryParameters.getAll("memberId")?.map(String::toUUID)
                        val roomIds = call.request.queryParameters.getAll("roomId")?.map(String::toUUID)

                        if (studyGroupIds == null && courseIds == null && memberIds == null && roomIds == null)
                            throw MissingRequestParameterException("period field")
                        val timetable = findTimetableOfDay(
                            studyGroupIds = studyGroupIds,
                            courseIds = courseIds,
                            memberIds = memberIds,
                            roomIds = roomIds,
                            weekOfYear = weekOfYear,
                            dayOfWeek = dayOfWeek,
                            sorting = call.request.queryParameters.getSortingBy(PeriodsSorting)
                        )
                        call.respond(HttpStatusCode.OK, timetable)
                    }
                    delete {
                        requireCapability(call.currentUserId(), Capability.WriteTimetable, config.organization.id)
                        val weekOfYear = call.parameters.getOrFail("weekOfYear")
                        val dayOfWeek = call.parameters.getOrFail("dayOfWeek").toInt()
                        removeTimetableOfDay(
                            call.request.queryParameters.getUuid("studyGroupId"),
                            weekOfYear,
                            dayOfWeek
                        )
                        call.respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}