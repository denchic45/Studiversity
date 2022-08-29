package com.denchic45.kts.data.pref

import com.russhwolf.settings.*
import com.russhwolf.settings.coroutines.getBooleanOrNullFlow
import com.russhwolf.settings.coroutines.getIntFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)
class AppPreferences(val settings: ObservableSettings) : Settings by settings {
    var lessonTime by int()
    var coursesLoadedFirstTime by boolean()
    var latestVersion by long()

    var token by nullableString()
    var refreshToken by nullableString()

    val observeLessonTime: Flow<Int> = settings.getIntFlow("lessonTime", 45)
    val observeToken: Flow<String?> = settings.getStringOrNullFlow("token")
}