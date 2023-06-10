package com.denchic45.studiversity.data.pref

import com.russhwolf.settings.*
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi


class UserPreferences(val settings: ObservableSettings) : Settings by settings {
    var id: String by string(defaultValue = "")
    var firstName: String by string(defaultValue = "")
    var surname: String by string(defaultValue = "")
    var patronymic: String by string(defaultValue = "")
    var gender: String by string(defaultValue = "")
    var role: String by string(defaultValue = "")
    var groupId: String by string(defaultValue = "")
    var avatarUrl: String by string(defaultValue = "")
    var isGeneratedAvatar: Boolean by boolean(defaultValue = true)
    var timestamp: Long by long(defaultValue = 0)
    var email: String by string(defaultValue = "")
    var isAdmin: Boolean by boolean(defaultValue = true)

    @OptIn(ExperimentalSettingsApi::class)
    val observeId = settings.getStringFlow("id",defaultValue = id)
}