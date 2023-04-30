package com.denchic45.kts.data.db.local

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.denchic45.kts.AppDatabase
import com.denchic45.kts.AttachmentEntity
import com.denchic45.kts.CourseContentEntity
import com.denchic45.kts.EventEntity
import com.denchic45.kts.SectionEntity
import com.denchic45.kts.StudyGroupEntity
import com.denchic45.kts.SubmissionEntity
import com.denchic45.kts.UserEntity
import com.denchic45.kts.data.mapper.ListMapper
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ListColumnAdapter : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String): List<String> {
        return ListMapper.tolList(databaseValue)
    }

    override fun encode(value: List<String>): String {
        return ListMapper.fromList(value)
    }
}

class LocalDateColumnAdapter : ColumnAdapter<LocalDate, String> {
    override fun decode(databaseValue: String): LocalDate {
        return LocalDate.parse(databaseValue)
    }

    override fun encode(value: LocalDate): String {
        return value.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
}

class DbHelper(val driver: SqlDriver) {
    val database by lazy {
//        val currentVer = version

//        if (currentVer == 0) {
//            AppDatabase.Schema.create(driver)
//            version = 1
//            println("init: created tables, setVersion to 1")
//        } else {
//            val schemaVer: Int = AppDatabase.Schema.version
//            if (schemaVer > currentVer) {
//                AppDatabase.Schema.migrate(driver, currentVer, schemaVer)
//                version = schemaVer
//                println("init: migrated from $currentVer to $schemaVer")
//            } else {
//                //println("init")
//            }
//        }
        AppDatabase(
            driver,
            courseContentEntityAdapter = CourseContentEntity.Adapter(
                attachmentsAdapter = ListColumnAdapter()
            ),
            submissionEntityAdapter = SubmissionEntity.Adapter(
                attachmentsAdapter = ListColumnAdapter()
            ),
            eventEntityAdapter = EventEntity.Adapter(
                teacher_idsAdapter = ListColumnAdapter(),
                positionAdapter = IntColumnAdapter
            ),
            userEntityAdapter = UserEntity.Adapter(
                EnumColumnAdapter()
            ),
            studyGroupEntityAdapter = StudyGroupEntity.Adapter(
                LocalDateColumnAdapter(),
                LocalDateColumnAdapter()
            ),
            attachmentEntityAdapter = AttachmentEntity.Adapter(
                EnumColumnAdapter()
            ),
            sectionEntityAdapter = SectionEntity.Adapter(
                orderAdapter = IntColumnAdapter
            )
        )
    }

    private var version: Int
        get() {
            val sqlCursor = driver.executeQuery(
                identifier = null,
                sql = "PRAGMA user_version;",
                mapper = { it.getLong(0)!!.toInt() },
                parameters = 0,
                binders = null
            )
            return sqlCursor.value
        }
        private set(version) {
            driver.execute(null, String.format("PRAGMA user_version = %d;", version), 0, null)
        }
}

suspend fun Transacter.suspendedTransaction(
    block: suspend () -> Unit
) = withContext(Dispatchers.IO) {
    transaction {
        launch {
            block()
        }
    }
}

suspend fun <T> Transacter.suspendedTransactionWithResult(block: suspend () -> T): T =
    withContext(Dispatchers.IO) {
        val result: Deferred<T> = transactionWithResult {
            async {
                block()
            }
        }
        result.await()
    }