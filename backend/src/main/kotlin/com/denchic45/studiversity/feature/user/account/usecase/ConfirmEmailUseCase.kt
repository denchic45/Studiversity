package com.denchic45.studiversity.feature.user.account.usecase

import com.denchic45.studiversity.feature.user.TokenRepository
import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class ConfirmEmailUseCase(
    private val transactionWorker: TransactionWorker,
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(token: UUID): Boolean = transactionWorker {
        val verificationToken = tokenRepository.findVerificationToken(token)
        if (verificationToken != null && verificationToken.expired || verificationToken == null) {
            false
        } else {
            userRepository.updateEmail(verificationToken.userId, verificationToken.payload)

            tokenRepository.removeVerificationToken(token)
            true
        }
    }
}