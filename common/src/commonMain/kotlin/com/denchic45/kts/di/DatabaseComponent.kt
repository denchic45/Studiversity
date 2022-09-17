package com.denchic45.kts.di

import com.denchic45.kts.AppDatabase
import com.denchic45.kts.data.db.local.DbHelper
import com.denchic45.kts.data.db.local.DriverFactory
import com.squareup.sqldelight.db.SqlDriver
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@AppScope
@Component
abstract class DatabaseComponent(@get:Provides val driverFactory: DriverFactory) {

    @AppScope
    @Provides
    fun provideSqlDriver(): SqlDriver {
        return driverFactory.driver
    }

    @AppScope
    @Provides
    fun provideDbHelper(sqlDriver: SqlDriver): DbHelper {
        return DbHelper(sqlDriver)
    }

    @AppScope
    @Provides
    fun provideAppDatabase(dbHelper: DbHelper): AppDatabase {
        return dbHelper.database
    }
}