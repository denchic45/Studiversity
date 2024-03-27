package com.denchic45.studiversity.feature.user.account.usecase

import com.denchic45.studiversity.api.account.model.UpdatePasswordRequest
import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class UpdatePasswordUseCase(
    private val transactionWorker: TransactionWorker,
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: UUID, request: UpdatePasswordRequest) = transactionWorker {
        userRepository.updatePassword(userId, request)
    }
}