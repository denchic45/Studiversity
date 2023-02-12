package com.studiversity.feature.auth.usecase

import com.studiversity.feature.user.UserRepository
import com.studiversity.transaction.TransactionWorker
import com.stuiversity.api.auth.AuthErrors
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