package com.denchic45.kts.ui.splash

import com.denchic45.kts.data.service.AuthService
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class SplashComponent(
    authService: AuthService,
) {
    val isAuth: Flow<Boolean> = authService.observeIsAuthenticated
}