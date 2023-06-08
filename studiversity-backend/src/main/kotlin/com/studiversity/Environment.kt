package com.studiversity

import com.sksamuel.hoplite.ConfigAlias
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import java.util.*

data class OrganizationConf(val id: UUID, val name:String,val selfRegister: Boolean, val initialized: Boolean)

data class JwtConf(val audience: String, val secret: String)

data class DatabaseConf(val url: String, val driver: String, val user: String, val password: String)

data class SmtpConf(
    val host: String,
    val port: Int,
    @ConfigAlias("use-ssl") val ssl: Boolean,
    val username: String,
    val password: String
)

data class SupabaseConf(val url: String, val key: String)

data class Config(
    val organization: OrganizationConf,
    val jwt: JwtConf,
    val database: DatabaseConf,
    val smtp: SmtpConf,
    val supabase: SupabaseConf
)

val config: Config
    get() = ConfigLoaderBuilder.default()
        .addResourceSource("/application.conf")
        .build()
        .loadConfigOrThrow()