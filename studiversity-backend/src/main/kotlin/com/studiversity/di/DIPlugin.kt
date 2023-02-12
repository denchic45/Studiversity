package com.studiversity.di

import com.studiversity.database.DatabaseFactory
import com.studiversity.database.DatabaseFactoryImpl
import com.studiversity.feature.attachment.attachmentModule
import com.studiversity.feature.auth.authModule
import com.studiversity.feature.course.courseModule
import com.studiversity.feature.membership.membershipModule
import com.studiversity.feature.role.roleModule
import com.studiversity.feature.room.roomModule
import com.studiversity.feature.studygroup.studyGroupModule
import com.studiversity.feature.timetable.timetableModule
import com.studiversity.feature.user.userModule
import com.studiversity.transaction.DatabaseTransactionWorker
import com.studiversity.transaction.SuspendTransactionWorker
import com.studiversity.transaction.TransactionWorker
import com.studiversity.util.EmailSender
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

val otherModule = module {
    single { CoroutineScope(SupervisorJob()) }
    single<DatabaseFactory> {
        DatabaseFactoryImpl(
            get(named(DatabaseEnv.DATABASE_URL)),
            get(named(DatabaseEnv.DATABASE_DRIVER)),
            get(named(DatabaseEnv.DATABASE_USER)),
            get(named(DatabaseEnv.DATABASE_PASSWORD)),
        )
    }
    single {
        EmailSender(
            get(named(SmtpEnv.SMTP_HOST)),
            get(named(SmtpEnv.SMTP_PORT)),
            get(named(SmtpEnv.SMTP_USE_SSL)),
            get(named(SmtpEnv.SMTP_USERNAME)),
            get(named(SmtpEnv.SMTP_PASSWORD)),
        )
    }
    factory { DatabaseTransactionWorker() } binds arrayOf(TransactionWorker::class, SuspendTransactionWorker::class)
}

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(
            otherModule,
            environmentModule,
            supabaseClientModule,
            authModule,
            userModule,
            roleModule,
            membershipModule,
            studyGroupModule,
            attachmentModule,
            courseModule,
            timetableModule,
            roomModule
        )
    }
}