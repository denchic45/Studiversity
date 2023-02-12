package com.studiversity.feature.auth.usecase

import com.studiversity.feature.user.UserRepository
import com.studiversity.transaction.SuspendTransactionWorker
import com.stuiversity.api.auth.AuthErrors
import com.stuiversity.api.auth.model.SignupRequest
import io.ktor.server.plugins.*

class SignUpUseCase(
    private val transactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(signupRequest: SignupRequest) = transactionWorker.suspendInvoke {
        if (userRepository.existByEmail(signupRequest.email)) throw BadRequestException(AuthErrors.USER_ALREADY_REGISTERED)
        userRepository.add(signupRequest)
    }
}