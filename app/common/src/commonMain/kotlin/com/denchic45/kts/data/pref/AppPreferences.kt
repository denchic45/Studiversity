package com.denchic45.kts.data.pref

import com.russhwolf.settings.*
import com.russhwolf.settings.coroutines.getIntFlow
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)
class AppPreferences(val settings: ObservableSettings) : Settings by settings {
//    var coursesLoadedFirstTime by boolean()
    var latestVersion by long(defaultValue = 0)

    var bellSchedule by string(defaultValue = "")

    var token by string(defaultValue = "")
    var refreshToken by string(defaultValue = "")

    var url by string( defaultValue = "")
    var organizationId by string(defaultValue = "")

    var yourStudyGroups by string(defaultValue = "")

    val observeToken = settings.getStringFlow("token","")
    var observeBellSchedule = settings.getStringFlow("bellSchedule","")
    var observeYourStudyGroups = settings.getStringOrNullFlow("yourStudyGroups")
}