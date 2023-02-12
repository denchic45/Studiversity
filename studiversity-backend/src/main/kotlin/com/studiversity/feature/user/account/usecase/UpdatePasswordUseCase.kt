package com.studiversity.feature.user.account.usecase

import com.studiversity.feature.user.UserRepository
import com.studiversity.transaction.TransactionWorker
import com.stuiversity.api.account.model.UpdatePasswordRequest
import java.util.*

class UpdatePasswordUseCase(
    private val transactionWorker: TransactionWorker,
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: UUID, updatePasswordRequest: UpdatePasswordRequest) = transactionWorker {
        userRepository.update(userId, updatePasswordRequest)
    }
}