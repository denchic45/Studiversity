package com.denchic45.studiversity.data.db.local

import app.cash.sqldelight.db.SqlDriver


expect class DriverFactory {
    val driver: SqlDriver
}