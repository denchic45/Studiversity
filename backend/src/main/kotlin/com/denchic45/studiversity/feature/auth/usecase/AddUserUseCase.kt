package com.denchic45.studiversity.feature.auth.usecase

import com.denchic45.studiversity.config.config
import com.denchic45.studiversity.feature.auth.PasswordGenerator
import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.logger.logger
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.studiversity.util.EmailSender
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import org.mindrot.jbcrypt.BCrypt

class AddUserUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val emailSender: EmailSender
) {
    suspend operator fun invoke(createUserRequest: CreateUserRequest) = suspendTransactionWorker.invoke {
        val password = PasswordGenerator().generate()
        logger.info { "generated password for user: ${createUserRequest.email}, password: $password" }
        val user = userRepository.add(createUserRequest, BCrypt.hashpw(password, BCrypt.gensalt()))
        roleRepository.addUserRolesInScope(user.id,createUserRequest.roleIds, config.organizationId)
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