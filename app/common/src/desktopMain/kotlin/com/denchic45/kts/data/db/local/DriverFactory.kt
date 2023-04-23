package com.denchic45.kts.data.db.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.denchic45.kts.util.SystemDirs
import java.io.File
import java.util.*

actual class DriverFactory {
    actual val driver: SqlDriver = run {
        val file = SystemDirs().appDir
        file.mkdirs()
        val url = "jdbc:sqlite:${file.path}${File.separator}database.db"
        val driver = JdbcSqliteDriver(url, Properties())
        driver
    }
}