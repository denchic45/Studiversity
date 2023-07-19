package com.denchic45.studiversity.feature.auth.usecase

import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.auth.AuthErrors
import io.ktor.server.plugins.*

class CheckMagicLinkTokenUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(token: String) = suspendTransactionWorker {
        val magicLinkToken = userRepository.findMagicLinkByToken(token)
            ?: throw BadRequestException(AuthErrors.INVALID_MAGIC_LINK)

//        userRepository.removeMagicLink(magicLinkToken.token)

        if (magicLinkToken.isExpired)
            throw BadRequestException(AuthErrors.INVALID_MAGIC_LINK)
    }
}