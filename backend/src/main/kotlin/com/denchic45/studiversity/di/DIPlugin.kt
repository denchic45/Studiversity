package com.denchic45.studiversity.di


import com.denchic45.studiversity.config.config
import com.denchic45.studiversity.database.DatabaseFactory
import com.denchic45.studiversity.database.DatabaseFactoryImpl
import com.denchic45.studiversity.feature.attachment.attachmentModule
import com.denchic45.studiversity.feature.auth.authModule
import com.denchic45.studiversity.feature.course.courseModule
import com.denchic45.studiversity.feature.role.roleModule
import com.denchic45.studiversity.feature.room.roomModule
import com.denchic45.studiversity.feature.specialty.specialtyModule
import com.denchic45.studiversity.feature.studygroup.studyGroupModule
import com.denchic45.studiversity.feature.timetable.timetableModule
import com.denchic45.studiversity.feature.user.userModule
import com.denchic45.studiversity.transaction.DatabaseSuspendedTransactionWorker
import com.denchic45.studiversity.transaction.DatabaseTransactionWorker
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

val otherModule = module {
    single { CoroutineScope(SupervisorJob()) }
    single<DatabaseFactory> {
        DatabaseFactoryImpl(
            config.dbUrl,
//            "org.postgresql.Driver",
            //            configuration2.driver,
            config.dbUser,
            config.dbPassword,
        )
    }
//    single {
//        EmailSender(
//            config.smtp.host,
//            config.smtp.port,
//            config.smtp.ssl,
//            config.smtp.username,
//            config.smtp.password,
//        )
//    }

    factory { DatabaseTransactionWorker() } binds arrayOf(TransactionWorker::class)

    factory { DatabaseSuspendedTransactionWorker() } binds arrayOf(SuspendTransactionWorker::class)
}

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(
            otherModule, authModule, userModule, roleModule,
//            membershipModule,
            studyGroupModule, specialtyModule, attachmentModule, courseModule, timetableModule, roomModule
        )
    }
}