package com.denchic45.studiversity.di

import com.denchic45.studiversity.data.pref.core.FilePreferences
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings

actual class SettingsFactory {
    actual fun create(name: String): Settings {
        return PreferencesSettings(FilePreferences(null, name))
    }

    actual fun createObservable(name: String): ObservableSettings {
        return PreferencesSettings(FilePreferences(null, name))
    }
}