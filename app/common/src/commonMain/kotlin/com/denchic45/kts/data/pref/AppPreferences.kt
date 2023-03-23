package com.denchic45.kts.data.pref

import com.russhwolf.settings.*
import com.russhwolf.settings.coroutines.getIntFlow
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)
class AppPreferences(val settings: ObservableSettings) : Settings by settings {
    var lessonTime by int()
    var coursesLoadedFirstTime by boolean()
    var latestVersion by long()

    var bellSchedule by string()

    var token by string(defaultValue = "")
    var refreshToken by string(defaultValue = "")

    var url by string()

    val observeLessonTime: Flow<Int> = settings.getIntFlow("lessonTime", 45)
    val observeToken: Flow<String?> = settings.getStringOrNullFlow("token")
    var observeBellSchedule: Flow<String> = settings.getStringFlow("bellSchedule")
}