package com.denchic45.studiversity.config

import java.io.File
import java.util.*
import kotlin.reflect.KProperty


val dataFile: File = File("").absoluteFile

val configFile = dataFile.resolve("config.properties")
    .apply { if (!exists()) {
        createNewFile()
        writeText("""
            jwtSecret=${UUID.randomUUID()}
            jwtRealm=sub
            jwtAudience=authenticated
        """.trimIndent())
    } }

val config by lazy { Configuration() }

class Configuration {
    private val properties = Properties().apply { load(configFile.inputStream()) }
    private fun <T> property(key: String? = null, defValue: T? = null) =
        DefaultConfigPropertyDelegate(properties, key, defValue)

    private fun <T : Any> property(key: String? = null, defValue: String? = null, map: (String) -> T) =
        MappedConfigPropertyDelegate(DefaultConfigPropertyDelegate(properties, key, defValue), map)

    var jwtAudience: String by property()
    var jwtSecret: String by property()
    var jwtRealm: String by property()

    var dbUrl: String by property()
    var dbUser: String by property()
    var dbPassword: String by property()

    var organizationId: UUID by property(map = UUID::fromString)
    var organizationName: String by property()

    var selfRegister: Boolean by property()

    var initialized: Boolean by property(defValue = false)
}

fun Configuration.database(url: String, user: String, password: String) {
    dbUrl = url
    dbUser = user
    dbPassword = password
}

private class MappedConfigPropertyDelegate<T>(
    private val delegate: DefaultConfigPropertyDelegate<String>,
    private val map: (String) -> T
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return map(delegate.getValue(thisRef, property))
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        delegate.setValue(thisRef, property, value.toString())
    }
}

private class DefaultConfigPropertyDelegate<T>(
    private val properties: Properties,
    private val key: String?,
    private val defValue: T?
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return properties.getOrDefault(key ?: property.name, defValue) as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        properties.setProperty(key ?: property.name, value.toString())
        properties.store(configFile.outputStream(), null)
    }
}