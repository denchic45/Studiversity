package com.denchic45.studiversity.di

import app.cash.sqldelight.db.SqlDriver
import com.denchic45.studiversity.AppDatabase
import com.denchic45.studiversity.data.db.local.DbHelper
import com.denchic45.studiversity.data.db.local.DriverFactory
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@LayerScope
@Component
abstract class DatabaseComponent(@get:Provides val driverFactory: DriverFactory) {

    @LayerScope
    @Provides
    fun provideSqlDriver(): SqlDriver {
        return driverFactory.driver
    }

    @LayerScope
    @Provides
    fun provideDbHelper(sqlDriver: SqlDriver): DbHelper {
        return DbHelper(sqlDriver)
    }

    @LayerScope
    @Provides
    fun provideAppDatabase(dbHelper: DbHelper): AppDatabase {
        return dbHelper.database
    }
}