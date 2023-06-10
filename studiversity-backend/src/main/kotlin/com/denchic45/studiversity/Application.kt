package com.denchic45.studiversity

import com.denchic45.stuiversity.api.OrganizationResponse
import com.denchic45.stuiversity.api.Pong
import com.denchic45.studiversity.database.DatabaseFactory
import com.denchic45.studiversity.di.configureDI
import com.denchic45.studiversity.feature.attachment.configureAttachments
import com.denchic45.studiversity.feature.auth.configureAuth
import com.denchic45.studiversity.feature.course.configureCourses
import com.denchic45.studiversity.feature.membership.configureMembership
import com.denchic45.studiversity.feature.role.configureRoles
import com.denchic45.studiversity.feature.room.configureRooms
import com.denchic45.studiversity.feature.schedule.configureSchedule
import com.denchic45.studiversity.feature.specialty.configureSpecialties
import com.denchic45.studiversity.feature.studygroup.configureStudyGroups
import com.denchic45.studiversity.feature.teacher.configureTeachers
import com.denchic45.studiversity.feature.timetable.configureTimetable
import com.denchic45.studiversity.feature.user.configureUsers
import com.denchic45.studiversity.plugin.configureRouting
import com.denchic45.studiversity.plugin.configureSerialization
import com.denchic45.studiversity.plugin.configureStatusPages
import com.denchic45.studiversity.supabase.configureSupabase
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
