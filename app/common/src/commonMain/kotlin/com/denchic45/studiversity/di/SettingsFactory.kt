package com.denchic45.studiversity.di

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings

expect class SettingsFactory {
    fun create(name: String): Settings
    fun createObservable(name: String): ObservableSettings
}