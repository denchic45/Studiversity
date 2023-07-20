package com.denchic45.studiversity.di

import com.denchic45.studiversity.data.preference.AppPreferences
import com.denchic45.studiversity.data.preference.TimestampPreferences
import com.denchic45.studiversity.data.preference.UserPreferences
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@LayerScope
@Component
abstract class PreferencesComponent(@get:Provides val factory: SettingsFactory) {
    @LayerScope
    @Provides
    fun provideAppPreferences(): AppPreferences {
        return AppPreferences(factory.createObservable("App"))
    }

    @LayerScope
    @Provides
    fun provideUserPreferences(): UserPreferences {
        return UserPreferences(factory.createObservable("User"))
    }

    @LayerScope
    @Provides
    fun provideTimestampPreferences(): TimestampPreferences {
        return TimestampPreferences(factory.createObservable("Timestamp"))
    }
}

