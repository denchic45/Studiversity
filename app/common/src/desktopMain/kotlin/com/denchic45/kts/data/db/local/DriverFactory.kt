package com.denchic45.kts.data.db.local

import com.denchic45.kts.util.SystemDirs
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.io.File
import java.util.*

actual class DriverFactory {
    actual val driver: SqlDriver = run {
        val file = SystemDirs().appDirectory
        file.mkdirs()
        val url = "jdbc:sqlite:${file.path}${File.separator}database.db"
        val driver = JdbcSqliteDriver(url, Properties())
        driver
    }
}