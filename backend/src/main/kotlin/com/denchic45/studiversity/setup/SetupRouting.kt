package com.denchic45.studiversity.setup

import com.denchic45.studiversity.config.config
import com.denchic45.studiversity.config.database
import com.denchic45.studiversity.database.DatabaseFactory
import com.denchic45.studiversity.database.table.*
import com.denchic45.studiversity.logger.logger
import com.denchic45.studiversity.setup.model.DatabaseSetupRequest
import com.denchic45.studiversity.setup.model.SetupErrors
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject

fun Application.configureSetup() {
    routing {
        singlePageApplication {
            useResources = true
            vue("web")
        }

        route("/setup") {
            val databaseFactory: DatabaseFactory by inject()

            // todo maybe useless
//            get { call.respond(config.initialized) }

            post("/database") {
                val body = call.receive<DatabaseSetupRequest>()
                config.database(body.url, body.user, body.password)

                val connected = transaction(databaseFactory.database) {
                    try {
                        !connection.isClosed
                    } catch (e: Exception) {
                        logger.error("database not connected")
                        if (e is ExposedSQLException) {
                            logger.error(
                                "state: ${e.sqlState}"
                            )
                        }
                        logger.error(e.message)
                        false
                    }
                }
                if (!connected) {
                    config.database("", "", "")
                    call.respond(HttpStatusCode.BadRequest, SetupErrors.INVALID_DATABASE)
                    return@post
                }

                SchemaUtils.createMissingTablesAndColumns(
                    // user tables
                    Users, UsersRolesScopes, Roles, Scopes, Capabilities, // 5
                    RolesScopes, RolesCapabilities, RolesAssignments, ScopeTypes, RefreshTokens, MagicLinks, // 6
                    // study group tables
                    StudyGroups, StudyGroupsMembers, Specialties, // 3
                    // courses tables
                    Courses, CourseTopics, CourseElements, CourseWorks, CoursesStudyGroups, // 5
                    Subjects, Submissions, Grades, Enrollments, UserEnrollments, // 5
                    // timetable
                    Events, Lessons, Periods, PeriodsMembers, Rooms, // 5
                    // attachments
                    Attachments, AttachmentReferences // 2
                )
            }

            post("/organization") {
                val body = call.receive<DatabaseSetupRequest>()

            }

            post("/admin") {

//                configuration2.apply {
//                    initialized = true
//                }
            }
        }
    }
}