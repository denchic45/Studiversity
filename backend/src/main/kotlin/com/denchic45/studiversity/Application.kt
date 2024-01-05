package com.denchic45.studiversity

import com.denchic45.studiversity.database.DatabaseFactory
import com.denchic45.studiversity.di.configureDI
import com.denchic45.studiversity.feature.attachment.configureAttachments
import com.denchic45.studiversity.feature.auth.configureAuth
import com.denchic45.studiversity.feature.course.configureCourses
import com.denchic45.studiversity.feature.role.configureRoles
import com.denchic45.studiversity.feature.room.configureRooms
import com.denchic45.studiversity.feature.schedule.configureSchedule
import com.denchic45.studiversity.feature.specialty.configureSpecialties
import com.denchic45.studiversity.feature.studygroup.configureStudyGroups
import com.denchic45.studiversity.feature.timetable.configureTimetable
import com.denchic45.studiversity.feature.user.configureUsers
import com.denchic45.studiversity.logger.logger
import com.denchic45.studiversity.plugin.configureRouting
import com.denchic45.studiversity.plugin.configureSerialization
import com.denchic45.studiversity.plugin.configureStatusPages
import com.denchic45.studiversity.supabase.configureSupabase
import com.denchic45.stuiversity.api.OrganizationResponse
import com.denchic45.stuiversity.api.Pong
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
    logger.info { "starting server..." }
    engine = embeddedServer(factory = Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun restartServer() {
    engine.stop()
    startServer()
}

@Suppress("unused")
fun Application.module() = runBlocking {
    logger.info { "starting started..." }
    install(PartialContent)
    install(AutoHeadResponse)
    configureDI()
    configureSerialization()
    configureStatusPages()
    logger.info { "initialized: ${config.organization.initialized}" }
    if (config.organization.initialized) {
        configurePing()
        logger.info { "configure database..." }
        configureDatabase()
        logger.info { "database configured" }
        logger.info { "configure supabase..." }
        configureSupabase()
        logger.info { "database configured" }
        configureAuth()
        logger.info { "auth configured" }
        configureUsers()
        configureRoles()
//        configureMembership()
        configureAttachments()
        configureStudyGroups()
        configureSpecialties()
        configureCourses()
        configureTimetable()
        configureSchedule()
        configureRooms()
        configureRouting()
        logger.info { "configuration success" }
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
