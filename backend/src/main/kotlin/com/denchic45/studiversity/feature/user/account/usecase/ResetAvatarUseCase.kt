package com.denchic45.studiversity.feature.user.account.usecase

import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class ResetAvatarUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: UUID
    ) = suspendTransactionWorker.invoke {
        userRepository.resetAvatar(userId)
    }
}