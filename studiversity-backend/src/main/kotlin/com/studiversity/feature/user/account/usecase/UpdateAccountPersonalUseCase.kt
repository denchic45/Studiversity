package com.studiversity.feature.user.account.usecase

import com.studiversity.feature.user.UserRepository
import com.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.account.model.UpdateAccountPersonalRequest
import java.util.*

class UpdateAccountPersonalUseCase(
    private val transactionWorker: TransactionWorker,
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: UUID, updateAccountPersonalRequest: UpdateAccountPersonalRequest) = transactionWorker {
        userRepository.update(userId, updateAccountPersonalRequest)
    }
}