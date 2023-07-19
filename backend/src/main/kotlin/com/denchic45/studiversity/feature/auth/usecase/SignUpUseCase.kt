package com.denchic45.studiversity.feature.auth.usecase

import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.auth.AuthErrors
import com.denchic45.stuiversity.api.auth.model.SignupRequest
import io.ktor.server.plugins.*

class SignUpUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(signupRequest: SignupRequest) = suspendTransactionWorker {
        if (userRepository.existByEmail(signupRequest.email)) throw BadRequestException(AuthErrors.USER_ALREADY_REGISTERED)
        userRepository.add(signupRequest)
    }
}