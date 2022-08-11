package com.denchic45.kts.ui.settings

import com.denchic45.kts.data.service.AuthService
import com.denchic45.kts.ui.base.BaseViewModel
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val authService: AuthService,
) : BaseViewModel() {
    fun onSignOutCLick() {
        authService.signOut()
    }
}