package com.denchic45.studiversity.data.pref

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getLongFlow
import com.russhwolf.settings.long
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)
class TimestampPreferences(val settings: ObservableSettings) : ObservableSettings by settings {
    var eventsUpdateTimestamp: Long by long(defaultValue = 0)
    var groupsUpdateTimestamp: Long by long(defaultValue = 0)
    var groupCoursesUpdateTimestamp: Long by long(defaultValue = 0)
    var teacherCoursesUpdateTimestamp: Long by long(defaultValue = 0)

    var observeGroupCoursesUpdateTimestamp =
        getLongFlow("groupCoursesUpdateTimestamp", defaultValue = 0)
}