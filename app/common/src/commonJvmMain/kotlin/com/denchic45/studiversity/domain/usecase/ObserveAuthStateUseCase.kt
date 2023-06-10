package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.service.AuthService
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveAuthStateUseCase(
    private val authService: AuthService,
) {

    operator fun invoke(): Flow<Boolean> {
        return authService.observeIsAuthenticated
    }
}