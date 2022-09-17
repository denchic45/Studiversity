package com.denchic45.kts.di

import com.denchic45.kts.data.pref.*
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@AppScope
@Component
abstract class PreferencesComponent(@get:Provides val factory: SettingsFactory) {
    @AppScope
    @Provides
    fun provideAppPreferences(): AppPreferences {
        return AppPreferences(factory.createObservable("App"))
    }

    @AppScope
    @Provides
    fun provideUserPreferences(): UserPreferences {
        return UserPreferences(factory.createObservable("User"))
    }

    @AppScope
    @Provides
    fun provideGroupPreferences(): GroupPreferences {
        return GroupPreferences(factory.createObservable("Group"))
    }

    @AppScope
    @Provides
    fun provideTimestampPreferences(): TimestampPreferences {
        return TimestampPreferences(factory.createObservable("Timestamp"))
    }

    @AppScope
    @Provides
    fun provideCoursePreferences(): CoursePreferences {
        return CoursePreferences(factory.createObservable("Courses"))
    }
}

