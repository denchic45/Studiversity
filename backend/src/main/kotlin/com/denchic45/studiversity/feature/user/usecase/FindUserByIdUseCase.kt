package com.denchic45.studiversity.feature.user.usecase

import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindUserByIdUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(userId: UUID) = suspendTransactionWorker.invoke {
        userRepository.findById(userId) ?: throw NotFoundException()
    }
}