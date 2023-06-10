package com.denchic45.studiversity.database

import org.jetbrains.exposed.sql.Database

interface DatabaseFactory {
    val database: Database
}

class DatabaseFactoryImpl(
    url: String,
    driver: String,
    user: String,
    password: String
) : DatabaseFactory {

    override val database by lazy {
        Database.connect(
            url = url,
            driver = driver,
            user = user,
            password = password
        )
    }
}