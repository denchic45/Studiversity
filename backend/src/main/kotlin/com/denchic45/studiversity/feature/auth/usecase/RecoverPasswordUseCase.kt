package com.denchic45.studiversity.feature.auth.usecase

import com.denchic45.studiversity.feature.user.TokenRepository
import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.studiversity.util.EmailSender
import com.denchic45.stuiversity.api.auth.AuthErrors
import io.ktor.server.plugins.*

class RecoverPasswordUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val emailSender: EmailSender
) {
    suspend operator fun invoke(email: String) = suspendTransactionWorker {
        val user = userRepository.findUserByEmail(email) ?: throw BadRequestException(AuthErrors.INVALID_EMAIL)
        val code = tokenRepository.generateCode(user.id, 600)

        emailSender.sendSimpleEmail(
            email,
            "Восстановление пароля",
            """
            Здравствуйте, ${user.firstName}!
            
            Вы отправили запрос на восстановление пароля.
            Для восстановления пароля введите код: $code
            (код действует 10 минут)
            
            Если вы не пытались восстановить пароль, игнорируйте данное письмо!
        """.trimIndent()
        )
    }
}