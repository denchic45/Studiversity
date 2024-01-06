package com.denchic45.studiversity

import com.denchic45.studiversity.config.configFile
import com.sksamuel.hoplite.ConfigAlias
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.addResourceSource
import java.util.*

data class OrganizationConf(val id: UUID, val name: String, val selfRegister: Boolean)

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
    val initialized: Boolean,
    val organization: OrganizationConf,
    val jwt: JwtConf,
    val database: DatabaseConf,
    val smtp: SmtpConf,
    val supabase: SupabaseConf
)

val config: Config
    get() = ConfigLoaderBuilder.default()
        .addFileSource(configFile)
        .build()
        .loadConfigOrThrow()