package com.denchic45.kts.data.pref

import com.russhwolf.settings.*
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi


class UserPreferences(val settings: ObservableSettings) : Settings by settings {

    var token: String by string()
    var refreshToken:String by string()

    var id: String by string()
    var firstName: String by string()
    var surname: String by string()
    var patronymic: String by string()
    var gender: String by string()
    var role: String by string()
    var groupId: String by string()
    var avatarUrl: String by string()
    var timestamp: Long by long()
    var email: String by string()
    var isGeneratedAvatar: Boolean by boolean(defaultValue = true)
    var isAdmin: Boolean by boolean(defaultValue = true)

    val observeId = settings.getStringFlow("id")
    val observeToken = settings.getStringOrNullFlow("token")
}