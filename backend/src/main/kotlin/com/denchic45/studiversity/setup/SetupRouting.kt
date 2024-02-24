package com.denchic45.studiversity.setup

import com.denchic45.studiversity.config.config
import com.denchic45.studiversity.config.database
import com.denchic45.studiversity.database.table.ScopeDao
import com.denchic45.studiversity.database.table.ScopeTypeDao
import com.denchic45.studiversity.feature.auth.usecase.SignUpUseCase
import com.denchic45.studiversity.feature.role.repository.ScopeRepository
import com.denchic45.studiversity.feature.role.usecase.SetRoleToUserInScopeUseCase
import com.denchic45.studiversity.logger.logger
import com.denchic45.studiversity.setup.model.DatabaseSetupRequest
import com.denchic45.studiversity.setup.model.OrganizationSetupRequest
import com.denchic45.studiversity.setup.model.SetupErrors
import com.denchic45.studiversity.util.respondWithError
import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.util.ErrorInfo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject
import java.sql.SQLException
import java.util.*


private var initializationCallback: (() -> Unit)? = null

fun onInitialized(block: () -> Unit) {
    initializationCallback = block
}

fun Application.configureSetup() {
    routing {
        singlePageApplication {
            useResources = true
            vue("setup-web")
        }

        val spaRoute = children.last()
        fun removeSPARoute() {
            (children as MutableList).remove(spaRoute)
        }

        route("/setup") {
            val scopeRepository: ScopeRepository by inject()
            val signup: SignUpUseCase by inject()
            val setRoleToUserInScope: SetRoleToUserInScopeUseCase by inject()

            post("/database") {
                val body = call.receive<DatabaseSetupRequest>()
                val driver = "postgresql"
                config.database(
                    url = "jdbc:$driver://${body.host}:${body.port}/${body.name}",
                    user = body.user,
                    password = body.password
                )
                requireDatabaseConnection()
                setupDatabase()
                call.respond(HttpStatusCode.OK)
            }

            post("/organization") {
                requireDatabaseConnection()
                val body = call.receive<OrganizationSetupRequest>()
                config.organizationName = body.name
                config.organizationId = UUID.randomUUID()

                ScopeDao.new(config.organizationId) {
                    type = ScopeTypeDao[1]
                    path = listOf(config.organizationId)
                }

//                config.selfRegister = body.selfRegister
                call.respond(HttpStatusCode.OK)
            }

            post("/admin") {
                requireDatabaseConnection()
                val user = signup(call.receive())
                setRoleToUserInScope(user.id, Role.Moderator.id, config.organizationId)

                removeSPARoute()
                initializationCallback?.invoke()
                call.respond(HttpStatusCode.OK)
                config.initialized = true
            }
        }
    }
}

private suspend fun PipelineContext<*, ApplicationCall>.requireDatabaseConnection() {
    try {
        transaction(Database.connect(url = config.dbUrl, user = config.dbUser, password = config.dbPassword)) {
            if (!connection.isClosed)
                logger.info("database successfully connected")
        }
    } catch (e: Exception) {
        logger.error("database not connected")
        if (e is SQLException) {
            logger.error(
                "database not connected: ${e.sqlState}"
            )
            call.respondWithError(
                HttpStatusCode.BadRequest,
                ErrorInfo(
                    when (e.sqlState) {
                        "28P01" -> SetupErrors.DATABASE_INVALID_PASSWORD
                        "08001" -> SetupErrors.DATABASE_CONNECTION_FAILED
                        "3D000" -> SetupErrors.DATABASE_DOES_NOT_EXIST
                        else -> SetupErrors.DATABASE_CONNECTION_FAILED
                    }
                )
            )
        }
        logger.error(e.message)
        throw e
    }
}