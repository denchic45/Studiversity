package com.denchic45.studiversity.feature.auth.usecase

import com.denchic45.studiversity.feature.user.TokenRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker

class CheckConfirmCodeUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(code: String) = suspendTransactionWorker {
        val confirmCode = tokenRepository.findConfirmCode(code) ?: return@suspendTransactionWorker false
        if (confirmCode.expired) return@suspendTransactionWorker false
        else true
    }
}