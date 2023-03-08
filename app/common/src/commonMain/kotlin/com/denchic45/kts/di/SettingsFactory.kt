package com.denchic45.kts.di

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings

expect class SettingsFactory {
    fun create(name: String): Settings
    @OptIn(ExperimentalSettingsApi::class)
    fun createObservable(name: String): ObservableSettings
}