package com.denchic45.kts.di

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings

actual class SettingsFactory(private val context: Context) {

    actual fun create(name: String): Settings {
        return AndroidSettings.Factory(context).create(name)
    }

    actual fun createObservable(name: String): ObservableSettings {
        return AndroidSettings(context.getSharedPreferences(name, Context.MODE_PRIVATE))
    }
}