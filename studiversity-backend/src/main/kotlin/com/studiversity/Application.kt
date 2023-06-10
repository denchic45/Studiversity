package com.studiversity

import com.denchic45.stuiversity.api.OrganizationResponse
import com.denchic45.stuiversity.api.Pong
import com.studiversity.database.DatabaseFactory
import com.studiversity.di.configureDI
import com.studiversity.feature.attachment.configureAttachments
import com.studiversity.feature.auth.configureAuth
import com.studiversity.feature.course.configureCourses
import com.studiversity.feature.membership.configureMembership
import com.studiversity.feature.role.configureRoles
import com.studiversity.feature.room.configureRooms
import com.studiversity.feature.schedule.configureSchedule
import com.studiversity.feature.specialty.configureSpecialties
import com.studiversity.feature.studygroup.configureStudyGroups
import com.studiversity.feature.teacher.configureTeachers
import com.studiversity.feature.timetable.configureTimetable
import com.studiversity.feature.user.configureUsers
import com.studiversity.plugin.configureRouting
import com.studiversity.plugin.configureSerialization
import com.studiversity.plugin.configureStatusPages
import com.studiversity.supabase.configureSupabase
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.koin.ktor.ext.inject

fun main() {
    startServer()
}

private lateinit var engine: ApplicationEngine

private fun startServer() {
    engine = embeddedServer(factory = Netty, port = 8080, host = "192.168.0.104", module = Application::module)
        .start(wait = true)
}

fun restartServer() {
    engine.stop()
    startServer()
}

@Suppress("unused")
fun Application.module() = runBlocking {
    install(PartialContent)
    install(AutoHeadResponse)
    configureDI()
    configureSerialization()
    configureStatusPages()
    if (config.organization.initialized) {
        configurePing()
        configureDatabase()
        configureSupabase()
        configureAuth()
        configureUsers()
        configureRoles()
        configureMembership()
        configureTeachers()
        configureAttachments()
        configureStudyGroups()
        configureSpecialties()
        configureCourses()
        configureTimetable()
        configureSchedule()
        configureRooms()
        configureRouting()
    }
}

fun Application.configurePing() {
    routing {
        get("/ping") {
            val organization = config.organization
            call.respond(
                Pong(
                    organization = OrganizationResponse(
                        id = organization.id,
                        name = organization.name,
                        allowRegistration = organization.selfRegister
                    )
                )
            )
        }
    }
}


private fun Application.configureDatabase() {
    val databaseFactory: DatabaseFactory by inject()
    databaseFactory.database
}
