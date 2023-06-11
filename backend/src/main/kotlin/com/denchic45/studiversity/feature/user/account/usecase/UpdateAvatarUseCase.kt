package com.denchic45.studiversity.feature.user.account.usecase

import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class UpdateAvatarUseCase(
    private val transactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: UUID,
        request: CreateFileRequest
    ) = transactionWorker.suspendInvoke {
        userRepository.updateAvatar(userId, request)
    }
}