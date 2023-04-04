package com.denchic45.kts.data.pref

import com.russhwolf.settings.*
import com.russhwolf.settings.coroutines.getIntFlow
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)
class AppPreferences(val settings: ObservableSettings) : Settings by settings {
//    var coursesLoadedFirstTime by boolean()
    var latestVersion by long()

    var bellSchedule by string()

    var token by string(defaultValue = "")
    var refreshToken by string(defaultValue = "")

    var url by string()

    var yourStudyGroups by string()

    val observeToken = settings.getStringOrNullFlow("token")
    var observeBellSchedule = settings.getStringFlow("bellSchedule")
    var observeYourStudyGroups = settings.getStringOrNullFlow("yourStudyGroups")
}