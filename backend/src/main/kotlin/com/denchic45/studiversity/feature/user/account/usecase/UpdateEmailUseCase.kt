package com.denchic45.studiversity.feature.user.account.usecase

import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.account.model.UpdateEmailRequest
import java.util.*

class UpdateEmailUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository
) {
  suspend operator fun invoke(userId: UUID, updateEmailRequest: UpdateEmailRequest) = suspendTransactionWorker {
        userRepository.update(userId, updateEmailRequest)
    }
}