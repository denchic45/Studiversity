package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.domain.Failure
import com.denchic45.kts.data.repository.UserRepository
import com.denchic45.kts.data.service.AuthService
import com.github.michaelbull.result.Result
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class SignInWithEmailAndPasswordUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authService: AuthService,
) {

    suspend operator fun invoke(email: String, password: String): Result<Unit, Failure> {
      return  authService.signInByEmailPassword(email, password)
//        userRepository.findAndSaveByEmail(email)
    }
}