package com.studiversity.feature.user.account.usecase

import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.studiversity.feature.user.UserRepository
import com.studiversity.transaction.SuspendTransactionWorker
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