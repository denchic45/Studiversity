package com.denchic45.kts.data.pref

import com.russhwolf.settings.*
import com.russhwolf.settings.coroutines.getIntFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)
class AppPreferences(val settings: ObservableSettings) : Settings by settings {
    var lessonTime by int()
    var coursesLoadedFirstTime by boolean()
    var latestVersion by long()

    var token by string()
    var refreshToken by string()

    val observeLessonTime: Flow<Int> = settings.getIntFlow("lessonTime", 45)
}