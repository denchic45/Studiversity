package com.denchic45.studiversity.feature.auth.usecase

import com.denchic45.studiversity.config
import com.denchic45.studiversity.feature.auth.PasswordGenerator
import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.logger.logger
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.studiversity.util.EmailSender
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import org.mindrot.jbcrypt.BCrypt

class SignUpUserManuallyUseCase(
    private val transactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val emailSender: EmailSender
) {
    suspend operator fun invoke(createUserRequest: CreateUserRequest) = transactionWorker.suspendInvoke {
        val password = PasswordGenerator().generate()
        logger.info { "generated password for user: ${createUserRequest.email}, password: $password" }
        val user = userRepository.add(createUserRequest, BCrypt.hashpw(password, BCrypt.gensalt()))
        roleRepository.addUserRolesToScope(user.id,createUserRequest.roleIds, config.organization.id)
        emailSender.sendSimpleEmail(
            createUserRequest.email,
            "Регистрация",
            generateEmailMessage(createUserRequest.firstName, createUserRequest.email, password)
        )
        user
    }

    private fun generateEmailMessage(firstName: String, email: String, password: String): String {
        return """
            Здравствуйте, $firstName
            
            Вы были успешно зарегистрированы! Ваши данные для авторизации:
            email: $email
            пароль: $password
        """.trimIndent()
    }
}