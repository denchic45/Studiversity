package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.service.AuthService
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