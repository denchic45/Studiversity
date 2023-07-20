package com.denchic45.studiversity.data.db.local

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.denchic45.studiversity.data.mapper.ListMapper
import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.Attachment
import com.denchic45.studiversity.entity.CourseContentEntity
import com.denchic45.studiversity.entity.CourseTopic
import com.denchic45.studiversity.entity.EventEntity
import com.denchic45.studiversity.entity.StudyGroup
import com.denchic45.studiversity.entity.Submission
import com.denchic45.studiversity.entity.User
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
    val database = AppDatabase(
        driver = driver,
        courseContentEntityAdapter = CourseContentEntity.Adapter(
            attachmentsAdapter = ListColumnAdapter()
        ),
        submissionAdapter = Submission.Adapter(
            attachmentsAdapter = ListColumnAdapter()
        ),
        eventEntityAdapter = EventEntity.Adapter(
            teacher_idsAdapter = ListColumnAdapter(),
            positionAdapter = IntColumnAdapter
        ),
        userAdapter = User.Adapter(EnumColumnAdapter()),
        studyGroupAdapter = StudyGroup.Adapter(
            start_academic_yearAdapter = LocalDateColumnAdapter(),
            end_academic_yearAdapter = LocalDateColumnAdapter()
        ),
        attachmentAdapter = Attachment.Adapter(EnumColumnAdapter()),
        courseTopicAdapter = CourseTopic.Adapter(IntColumnAdapter)
    )

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