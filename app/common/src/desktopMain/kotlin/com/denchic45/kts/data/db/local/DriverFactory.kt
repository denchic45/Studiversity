package com.denchic45.kts.data.db.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.denchic45.kts.AppDatabase
import com.denchic45.kts.util.SystemDirs
import java.io.File
import java.util.Properties

actual class DriverFactory {
    actual val driver: SqlDriver = run {
        val file = SystemDirs().appDir
        file.mkdirs()
        val databasePath = "${file.path}${File.separator}database.db"
        val url = "jdbc:sqlite:$databasePath"
        val driver = JdbcSqliteDriver(url, Properties())
        if (!File(databasePath).exists())
            AppDatabase.Schema.create(driver)
        driver
    }
}