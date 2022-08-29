package com.denchic45.kts.di.component

import com.denchic45.kts.data.pref.*
import com.denchic45.kts.di.SettingsFactory
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope

@PreferencesScope
@Component
abstract class PreferencesComponent(@get:Provides val factory: SettingsFactory) {
    @PreferencesScope
    @Provides
    fun provideAppPreferences(): AppPreferences {
        return AppPreferences(factory.createObservable("App"))
    }
    @PreferencesScope
    @Provides
    fun provideUserPreferences(): UserPreferences {
        return UserPreferences(factory.createObservable("User"))
    }
    @PreferencesScope
    @Provides
    fun provideGroupPreferences(): GroupPreferences {
        return GroupPreferences(factory.createObservable("Group")
        )
    }
    @PreferencesScope
    @Provides
    fun provideTimestampPreferences(): TimestampPreferences {
        return TimestampPreferences(factory.createObservable("Timestamp"))
    }
    @PreferencesScope
    @Provides
    fun provideCoursePreferences(): CoursePreferences {
        return CoursePreferences(factory.createObservable("Courses"))
    }
}

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class PreferencesScope