package com.denchic45.kts.data.pref

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getLongFlow
import com.russhwolf.settings.long
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)
class TimestampPreferences(val settings: ObservableSettings) : ObservableSettings by settings {
    var eventsUpdateTimestamp: Long by long()
    var groupsUpdateTimestamp: Long by long()
    var groupCoursesUpdateTimestamp: Long by long()
    var teacherCoursesUpdateTimestamp: Long by long()

    var observeGroupCoursesUpdateTimestamp = getLongFlow("groupCoursesUpdateTimestamp")
}