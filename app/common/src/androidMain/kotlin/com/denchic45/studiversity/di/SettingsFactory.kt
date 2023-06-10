package com.denchic45.studiversity.di

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings

actual class SettingsFactory(private val context: Context) {

    actual fun create(name: String): Settings {
        return SharedPreferencesSettings.Factory(context).create(name)
    }

    actual fun createObservable(name: String): ObservableSettings {
        return SharedPreferencesSettings(context.getSharedPreferences(name, Context.MODE_PRIVATE))
    }
}