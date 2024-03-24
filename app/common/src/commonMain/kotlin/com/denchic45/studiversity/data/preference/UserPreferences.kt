package com.denchic45.studiversity.data.preference

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.boolean
import com.russhwolf.settings.coroutines.getLongFlow
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.string


@OptIn(ExperimentalSettingsApi::class)
class UserPreferences(val settings: ObservableSettings) : Settings by settings {
    var id: String by string(defaultValue = "")
    var firstName: String by string(defaultValue = "")
    var surname: String by string(defaultValue = "")
    var patronymic: String by string(defaultValue = "")
    var gender: String by string(defaultValue = "")
    var avatarUrl: String by string(defaultValue = "")
    var isGeneratedAvatar: Boolean by boolean(defaultValue = true)
    var email: String by string(defaultValue = "")

    val observeId = settings.getStringFlow("id", defaultValue = id)
    val observeLastUpdateTimestamp = settings.getLongFlow("lastUpdateTimestamp", 0)
}