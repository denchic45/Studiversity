package com.denchic45.kts.di

import com.denchic45.kts.data.pref.core.FilePreferences
import com.russhwolf.settings.JvmPreferencesSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings

actual class SettingsFactory {
    actual fun create(name: String): Settings {
        return JvmPreferencesSettings(FilePreferences(null, name))
    }

    actual fun createObservable(name: String): ObservableSettings {
        return JvmPreferencesSettings(FilePreferences(null, name))
    }

}