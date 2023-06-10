package com.denchic45.studiversity.feature.user.account.usecase

import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.TransactionWorker
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