package com.studiversity.feature.user.account.usecase

import com.studiversity.feature.user.UserRepository
import com.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.account.model.UpdateEmailRequest
import java.util.*

class UpdateEmailUseCase(
    private val transactionWorker: TransactionWorker,
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: UUID, updateEmailRequest: UpdateEmailRequest) = transactionWorker {
        userRepository.update(userId, updateEmailRequest)
    }
}