package com.denchic45.studiversity

import com.denchic45.studiversity.config.config
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
import com.denchic45.studiversity.setup.configureSetup
import com.denchic45.studiversity.setup.onInitialized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.partialcontent.*
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
//    environment.config.propertyOrNull("database.url")
    logger.info { "starting started..." }
    install(PartialContent)
    install(AutoHeadResponse)
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }
    configureDI()
    configureSerialization()
    configureStatusPages()
//    logger.info { "initialized: ${config.initialized}" }
//    if (config.initialized) {
    if (config.initialized) {
        configureServer()
    } else {
        configureSetup()
        // todo реализовать ожидание инициализации
        onInitialized(::configureServer)
    }
}

private fun Application.configureServer() {
    configurePing()
    configureDatabase()
    logger.info { "database configured" }
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



//data class InitRequest(
//    val dbUrl: String,
//    val dbUser: String,
//    val dbPassword: String,
//
//    val organizationName: String
//)

fun Application.configurePing() {
    routing {
        get("/ping") {
            // todo вернуть как было
//            val organization = config.organization
//            call.respond(
//                Pong(
//                    organization = OrganizationResponse(
//                        id = organization.id,
//                        name = organization.name,
//                        allowRegistration = organization.selfRegister
//                    )
//                )
//            )
        }
    }
}


fun Application.configureDatabase() {
    logger.info { "configure database..." }
    val databaseFactory: DatabaseFactory by inject()
    databaseFactory.database
}