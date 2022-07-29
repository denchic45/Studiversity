package com.denchic45.kts.data.local.db

import com.denchic45.kts.AppDatabase
import com.denchic45.kts.CourseContentEntity
import com.denchic45.kts.EventEntity
import com.denchic45.kts.SubmissionEntity
import com.denchic45.kts.data.mapper.ListMapper
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.db.use

class ListColumnAdapter : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String): List<String> {
        return ListMapper.tolList(databaseValue)
    }

    override fun encode(value: List<String>): String {
        return ListMapper.fromList(value)
    }
}

class DbHelper(val driver: SqlDriver) {

    val database by lazy {
        val currentVer = version
        if (currentVer == 0) {
            AppDatabase.Schema.create(driver)
            version = 1
            println("init: created tables, setVersion to 1")
        } else {
            val schemaVer: Int = AppDatabase.Schema.version
            if (schemaVer > currentVer) {
                AppDatabase.Schema.migrate(driver, currentVer, schemaVer)
                version = schemaVer
                println("init: migrated from $currentVer to $schemaVer")
            } else {
                //println("init")
            }
        }
        AppDatabase(
            driver,
            courseContentEntityAdapter = CourseContentEntity.Adapter(
                attachmentsAdapter = object : ColumnAdapter<List<String>, String> {
                    override fun decode(databaseValue: String): List<String> {
                        return ListMapper.tolList(databaseValue)
                    }

                    override fun encode(value: List<String>): String {
                        return ListMapper.fromList(value)
                    }
                }
            ),
            submissionEntityAdapter = SubmissionEntity.Adapter(
                attachmentsAdapter = ListColumnAdapter()
            ),
        eventEntityAdapter = EventEntity.Adapter(
            teacher_idsAdapter = ListColumnAdapter()
        ))
    }

    private var version: Int
        get() {
            val sqlCursor = driver.executeQuery(null, "PRAGMA user_version;", 0, null)
            sqlCursor.next()
            return sqlCursor.getLong(0)!!.toInt()
        }
        private set(version) {
            driver.execute(null, String.format("PRAGMA user_version = %d;", version), 0, null)
        }
}