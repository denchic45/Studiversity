package com.studiversity.feature.user.account.usecase

import com.studiversity.feature.user.UserRepository
import com.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class RemoveAvatarUseCase(
    private val transactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: UUID
    ) = transactionWorker.suspendInvoke {
        userRepository.deleteAvatar(userId)
    }
}