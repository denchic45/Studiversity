package com.denchic45.kts.di.module

import com.denchic45.kts.data.pref.*
import com.denchic45.kts.di.SettingsFactory
import dagger.Module
import dagger.Provides

@Module
class PreferencesModule(private val factory: SettingsFactory) {

    @Provides
    fun provideAppPreferences(): AppPreferences {
        return AppPreferences(factory.createObservable("App"))
    }

    @Provides
    fun provideUserPreferences(): UserPreferences {
        return UserPreferences(factory.createObservable("User"))
    }

    @Provides
    fun provideTimestampPreferences(): TimestampPreferences {
        return TimestampPreferences(factory.createObservable("Timestamp"))
    }

    @Provides
    fun provideCoursePreferences(): CoursePreferences {
        return CoursePreferences(factory.createObservable("Courses"))
    }
}