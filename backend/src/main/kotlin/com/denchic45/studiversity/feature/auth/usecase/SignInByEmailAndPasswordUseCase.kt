package com.denchic45.studiversity.feature.auth.usecase

import com.denchic45.studiversity.feature.auth.model.RefreshToken
import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.auth.AuthErrors
import com.denchic45.stuiversity.api.auth.model.SignInByEmailPasswordRequest
import io.ktor.server.plugins.*
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class SignInByEmailAndPasswordUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(signInByEmailPasswordRequest: SignInByEmailPasswordRequest) = suspendTransactionWorker {
        val userByEmail = userRepository.findEmailPasswordByEmail(signInByEmailPasswordRequest.email)
            ?: throw BadRequestException(AuthErrors.INVALID_EMAIL)
        if (!BCrypt.checkpw(signInByEmailPasswordRequest.password, userByEmail.password))
            throw BadRequestException(AuthErrors.INVALID_PASSWORD)

        userByEmail.id to userRepository.addToken(
            RefreshToken(
                userByEmail.id,
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusWeeks(1).toInstant(ZoneOffset.UTC)
            )
        )
    }
}