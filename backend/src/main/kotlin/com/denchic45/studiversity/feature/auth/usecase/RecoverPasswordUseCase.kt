package com.denchic45.studiversity.feature.auth.usecase

import com.denchic45.studiversity.feature.auth.model.MagicLinkToken
import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.studiversity.util.EmailSender
import com.denchic45.stuiversity.api.auth.AuthErrors
import com.denchic45.stuiversity.api.auth.model.ResetPasswordRequest
import io.ktor.server.plugins.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class RecoverPasswordUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository,
    private val emailSender: EmailSender
) {
    suspend operator fun invoke(recoverPasswordRequest: ResetPasswordRequest, url: String) = suspendTransactionWorker {
        val email = recoverPasswordRequest.email
        val user = userRepository.findUserByEmail(email)
            ?: throw BadRequestException(AuthErrors.INVALID_EMAIL)
        val token = userRepository.addMagicLink(
            MagicLinkToken(
                userId = user.id,
                token = UUID.randomUUID().toString(),
                expireAt = LocalDateTime.now().plusMinutes(15).toInstant(ZoneOffset.UTC)
            )
        )
        emailSender.sendSimpleEmail(
            email,
            "Восстановление пароля",
            """
            Здравствуйте, ${user.firstName}!
            
            Вы отправили запрос на восстановление пароля. Для восстановления пароля перейдите по ссылке
            $url?token=$token
            (ссылка действительна 15 минут)
            
            Если вы не пытались восстановить пароль, игнорируйте данное письмо!
        """.trimIndent()
        )
    }
}