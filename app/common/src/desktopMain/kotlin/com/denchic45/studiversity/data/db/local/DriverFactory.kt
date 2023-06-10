package com.denchic45.studiversity.data.db.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.denchic45.studiversity.AppDatabase
import com.denchic45.studiversity.util.SystemDirs
import okio.FileSystem
import java.util.Properties

actual class DriverFactory() {
    actual val driver: SqlDriver = run {
        val dir = SystemDirs().appDir / "databases"
        FileSystem.SYSTEM.createDirectory(dir)
        val databasePath = dir / "database.db"
        val url = "jdbc:sqlite:$databasePath"
        val driver = JdbcSqliteDriver(url, Properties())
        if (!FileSystem.SYSTEM.exists(databasePath))
            AppDatabase.Schema.create(driver)
        driver
    }
}