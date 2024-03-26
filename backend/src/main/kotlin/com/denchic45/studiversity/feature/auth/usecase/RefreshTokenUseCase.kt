package com.denchic45.studiversity.feature.auth.usecase

import com.denchic45.studiversity.feature.auth.model.RefreshToken
import com.denchic45.studiversity.feature.user.TokenRepository
import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.auth.AuthErrors
import com.denchic45.stuiversity.api.auth.model.RefreshTokenRequest
import io.ktor.server.plugins.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class RefreshTokenUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(refreshTokenRequest: RefreshTokenRequest) = suspendTransactionWorker {
        val foundRefreshToken = tokenRepository.findRefreshToken(refreshTokenRequest.refreshToken)
            ?: throw BadRequestException(AuthErrors.INVALID_REFRESH_TOKEN)

        tokenRepository.removeRefreshToken(refreshTokenRequest.refreshToken)

        if (foundRefreshToken.isExpired)
            throw BadRequestException(AuthErrors.INVALID_REFRESH_TOKEN)

        val userId = foundRefreshToken.userId
        val generatedRefreshToken = RefreshToken(
            userId,
            UUID.randomUUID().toString(),
            LocalDateTime.now().plusWeeks(1).toInstant(ZoneOffset.UTC)
        )
        userId to tokenRepository.addRefreshToken(generatedRefreshToken)
    }
}