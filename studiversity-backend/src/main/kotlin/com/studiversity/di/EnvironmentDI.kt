package com.studiversity.di

import com.studiversity.util.toUUID
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val Application.environmentModule: Module
    get() = module {
        val config = this@environmentModule.environment.config

        single(named(OrganizationEnv.ORG_ID)) { config.property("organization.id").getString().toUUID() }
        single(named(OrganizationEnv.ORG_SELF_REGISTER)) { config.property("organization.selfRegister").getString().toBoolean() }

        single(named(JwtEnv.JWT_AUDIENCE)) { config.property("jwt.audience").getString() }
        single(named(JwtEnv.JWT_DOMAIN)) { config.property("jwt.domain").getString() }
        single(named(JwtEnv.JWT_SECRET)) { config.property("jwt.secret").getString() }

        single(named(DatabaseEnv.DATABASE_URL)) { config.property("database.url").getString() }
        single(named(DatabaseEnv.DATABASE_DRIVER)) { config.property("database.driver").getString() }
        single(named(DatabaseEnv.DATABASE_USER)) { config.property("database.user").getString() }
        single(named(DatabaseEnv.DATABASE_PASSWORD)) { config.property("database.password").getString() }

        single(named(SmtpEnv.SMTP_HOST)) { config.property("smtp.host").getString() }
        single(named(SmtpEnv.SMTP_PORT)) { config.property("smtp.port").getString().toInt() }
        single(named(SmtpEnv.SMTP_USE_SSL)) { config.property("smtp.use-ssl").getString().toBoolean() }
        single(named(SmtpEnv.SMTP_USERNAME)) { config.property("smtp.username").getString() }
        single(named(SmtpEnv.SMTP_PASSWORD)) { config.property("smtp.password").getString() }

        single(named(SupabaseEnv.SUPABASE_URL)) { config.property("supabase.url").getString() }
        single(named(SupabaseEnv.SUPABASE_KEY)) { config.property("supabase.key").getString() }
    }

enum class OrganizationEnv { ORG_ID, ORG_SELF_REGISTER }

enum class JwtEnv { JWT_AUDIENCE, JWT_DOMAIN, JWT_SECRET }

enum class DatabaseEnv { DATABASE_URL, DATABASE_DRIVER, DATABASE_USER, DATABASE_PASSWORD }

enum class SmtpEnv { SMTP_HOST, SMTP_PORT, SMTP_USE_SSL, SMTP_USERNAME, SMTP_PASSWORD }

enum class SupabaseEnv { SUPABASE_URL, SUPABASE_KEY }