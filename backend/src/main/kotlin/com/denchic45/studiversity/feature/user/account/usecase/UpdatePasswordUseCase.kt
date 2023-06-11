package com.denchic45.studiversity.feature.user.account.usecase

import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.account.model.UpdatePasswordRequest
import java.util.*

class UpdatePasswordUseCase(
    private val transactionWorker: TransactionWorker,
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: UUID, updatePasswordRequest: UpdatePasswordRequest) = transactionWorker {
        userRepository.update(userId, updatePasswordRequest)
    }
}