package com.denchic45.kts.di

import com.denchic45.kts.data.pref.*
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
    fun provideGroupPreferences(): GroupPreferences {
        return GroupPreferences(factory.createObservable("Group"))
    }

    @LayerScope
    @Provides
    fun provideTimestampPreferences(): TimestampPreferences {
        return TimestampPreferences(factory.createObservable("Timestamp"))
    }

    @LayerScope
    @Provides
    fun provideCoursePreferences(): CoursePreferences {
        return CoursePreferences(factory.createObservable("Courses"))
    }
}

