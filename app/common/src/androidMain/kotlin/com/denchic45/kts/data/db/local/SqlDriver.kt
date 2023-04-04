package com.denchic45.kts.data.db.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.denchic45.kts.AppDatabase

actual class DriverFactory(context: Context) {
    actual val driver: SqlDriver = AndroidSqliteDriver(
        AppDatabase.Schema,
        context,
        "database.db"
    )
}