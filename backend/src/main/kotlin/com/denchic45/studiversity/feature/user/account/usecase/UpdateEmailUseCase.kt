package com.denchic45.studiversity.feature.user.account.usecase

import com.denchic45.studiversity.feature.user.TokenRepository
import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.studiversity.util.EmailSender
import io.ktor.server.plugins.*
import java.util.*

class UpdateEmailUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val emailSender: EmailSender
) {
    suspend operator fun invoke(userId: UUID, email: String, url: String) = suspendTransactionWorker {
        val generatedToken = tokenRepository.generateVerificationToken(userId, email, 1800)
        val user = userRepository.findById(userId) ?: throw NotFoundException()
        emailSender.sendSimpleEmail(
            email,
            "Подтверждение почты",
            """
            Здравствуйте, ${user.firstName}!
            
            Для подтверждения пароля перейдите по ссылке
            $url?token=$generatedToken
            (ссылка действительна 30 минут)
            
            Если вы не пытались обновить почту, ничего не делайте!
        """.trimIndent()
        )
    }
}