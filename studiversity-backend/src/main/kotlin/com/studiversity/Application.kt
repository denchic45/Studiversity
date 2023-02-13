package com.studiversity

import com.studiversity.database.DatabaseFactory
import com.studiversity.di.OrganizationEnv
import com.studiversity.di.configureDI
import com.studiversity.feature.auth.configureAuth
import com.studiversity.feature.membership.configureMembership
import com.studiversity.feature.role.configureRoles
import com.studiversity.feature.room.configureRoom
import com.studiversity.feature.timetable.configureTimetable
import com.studiversity.feature.user.configureUser
import com.studiversity.plugin.*
import com.studiversity.supabase.configureSupabase
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.coroutines.runBlocking
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused")
fun Application.module() = runBlocking {
    val initialized: Boolean by inject(named(OrganizationEnv.ORG_INIT))
    if (initialized)
        return@runBlocking
    configureDI()
    configureSerialization()
    configureStatusPages()
    configureDatabase()
    configureSupabase()
    configureAuth()
    configureUser()
    configureRoles()
    configureMembership()
    configureTimetable()
    configureRoom()
    configureRouting()
}

private fun Application.configureDatabase() {
    val databaseFactory: DatabaseFactory by inject()
    databaseFactory.database
}
