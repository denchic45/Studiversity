package com.denchic45.studiversity.ui.splash

import com.denchic45.studiversity.data.service.AuthService
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class SplashComponent(
    authService: AuthService,
) {
    val isAuth: Flow<Boolean> = authService.observeIsAuthenticated
}