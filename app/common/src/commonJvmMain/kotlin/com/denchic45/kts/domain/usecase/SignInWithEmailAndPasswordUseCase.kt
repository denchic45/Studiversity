package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.UserRepository
import com.denchic45.kts.data.service.AuthService
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class SignInWithEmailAndPasswordUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authService: AuthService,
) {

    suspend operator fun invoke(email: String, password: String) {
        println("AUTH: try...")
        authService.signInWithEmailAndPassword(email, password)
        userRepository.findAndSaveByEmail(email)
    }
}