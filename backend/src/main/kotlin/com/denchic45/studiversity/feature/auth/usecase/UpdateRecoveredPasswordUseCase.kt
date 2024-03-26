package com.denchic45.studiversity.feature.auth.usecase

import com.denchic45.studiversity.feature.user.TokenRepository
import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker

class UpdateRecoveredPasswordUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(code: String, password: String) = suspendTransactionWorker {
        userRepository.updateRecoveredPassword(tokenRepository.findConfirmCode(code)!!.userId, password)
        tokenRepository.removeConfirmCode(code)
    }
}