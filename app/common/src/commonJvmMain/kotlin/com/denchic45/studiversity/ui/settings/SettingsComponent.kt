package com.denchic45.studiversity.ui.settings

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.data.service.AuthService
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SettingsComponent(
    private val authService: AuthService,
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {

    fun onSignOutClick() {
        authService.signOut()
    }
}