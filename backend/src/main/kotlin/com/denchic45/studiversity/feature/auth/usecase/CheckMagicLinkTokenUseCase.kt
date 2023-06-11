package com.denchic45.studiversity.feature.auth.usecase

import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.auth.AuthErrors
import io.ktor.server.plugins.*

class CheckMagicLinkTokenUseCase(
    private val transactionWorker: TransactionWorker,
    private val userRepository: UserRepository
) {
    operator fun invoke(token: String) = transactionWorker {
        val magicLinkToken = userRepository.findMagicLinkByToken(token)
            ?: throw BadRequestException(AuthErrors.INVALID_MAGIC_LINK)

//        userRepository.removeMagicLink(magicLinkToken.token)

        if (magicLinkToken.isExpired)
            throw BadRequestException(AuthErrors.INVALID_MAGIC_LINK)
    }
}