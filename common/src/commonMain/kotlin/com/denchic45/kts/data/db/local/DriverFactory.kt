package com.denchic45.kts.data.db.local

import com.squareup.sqldelight.db.SqlDriver

expect class DriverFactory {
    val driver: SqlDriver
}