package com.denchic45.studiversity.config

import java.io.File
import java.util.Properties
import kotlin.reflect.KProperty


val dataFile: File = File("").absoluteFile

//val configFile = dataFile.resolve("application.conf")
val configFile = dataFile.resolve("config.properties")
    .apply { if (!exists()) createNewFile() }

val configuration2 by lazy { Configuration() }

class Configuration {
    private val properties = Properties().apply { load(configFile.inputStream()) }

    //    private val preferences = FilePreferences(null, "config")
    private fun <T> property(key: String, defValue: T? = null) = ConfigPropertyDelegate(properties, key, defValue)

//    var dbUrl: String
//        get() = preferences.get("db_url", "")
//        set(value) = preferences.put("db_url", value)

//    var dbUser: String
//        get() = preferences.get("db_user", "")
//        set(value) = preferences.put("db_user", value)

//    var dbPassword: String
//        get() = preferences.get("db_password", "")
//        set(value) = preferences.put("db_password", value)

    var dbUrl: String by property("db_url")
    var dbUser: String by property("db_user")
    var dbPassword: String by property("db_password")

    var initialized: Boolean by property("initialized", false)
}

private class ConfigPropertyDelegate<T>(
    private val properties: Properties,
    private val name: String,
    private val defValue: T?
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return properties.getOrDefault(name, defValue) as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        properties[name] = value
    }
}