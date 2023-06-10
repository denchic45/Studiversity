package com.denchic45.studiversity.ui.settings

import com.denchic45.studiversity.data.service.AuthService
import com.denchic45.studiversity.ui.base.BaseViewModel
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
//    private val authService: AuthService,
) : BaseViewModel() {
    fun onSignOutCLick() {
//        authService.signOut()
    }
}