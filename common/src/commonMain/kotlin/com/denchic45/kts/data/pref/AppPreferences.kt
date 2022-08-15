package com.denchic45.kts.data.pref

import com.russhwolf.settings.*
import com.russhwolf.settings.coroutines.getIntFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)
class AppPreferences(val settings: ObservableSettings) : Settings by settings {
    var lessonTime: Int by int()
    var coursesLoadedFirstTime: Boolean by boolean()
    var latestVersion: Long by long()

    val observeLessonTime: Flow<Int> = settings.getIntFlow("lessonTime", 45)
}