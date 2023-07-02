package com.denchic45.studiversity.data.db.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.denchic45.studiversity.AppDatabase
import com.denchic45.studiversity.util.SystemDirs
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okio.FileSystem
import java.util.Properties

actual class DriverFactory {
    actual val driver: SqlDriver = run {
        val dir = SystemDirs().appDir / "databases"
        val system = FileSystem.SYSTEM
        system.createDirectory(dir)
        val databasePath = dir / "database.db"
        val databaseFile = databasePath.toFile()
        val url = "jdbc:sqlite:${databaseFile.absolutePath}"
        val driver = JdbcSqliteDriver(url, Properties())
        val exists = system.exists(databasePath)
        println("Exists: $exists")
        println("path: $databasePath")
        if (!exists) {
            GlobalScope.launch {
                AppDatabase.Schema.create(driver)
            }
        }
        println("version: ${AppDatabase.Schema.version}")

        driver
    }
}