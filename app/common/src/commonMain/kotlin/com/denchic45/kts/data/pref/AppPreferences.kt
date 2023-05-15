package com.denchic45.kts.data.pref

import com.russhwolf.settings.*
import com.russhwolf.settings.coroutines.getIntFlow
import com.russhwolf.settings.coroutines.getIntOrNullFlow
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSettingsApi::class)
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
    val observeBellSchedule = settings.getStringFlow("bellSchedule","")
    val observeYourStudyGroups = settings.getStringOrNullFlow("yourStudyGroups")

    var selectedStudyGroupTimetableId by nullableString()
    val selectedStudyGroupTimetableIdFlow = settings.getStringOrNullFlow("selectedStudyGroupTimetableId")

    var selectedStudyGroupId by nullableString()
    val selectedStudyGroupIdFlow = settings.getStringOrNullFlow("selectedStudyGroupId")
}