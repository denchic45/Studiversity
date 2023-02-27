package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.service.AuthService
import com.denchic45.kts.domain.EmptyResource
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class SignInWithEmailAndPasswordUseCase @Inject constructor(
    private val authService: AuthService,
) {

    suspend operator fun invoke(email: String, password: String): EmptyResource {
        return authService.signInByEmailPassword(email, password)
    }
}