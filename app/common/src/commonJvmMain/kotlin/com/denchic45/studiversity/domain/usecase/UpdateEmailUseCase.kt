package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.api.common.EmptyResponseResult
import com.denchic45.studiversity.data.service.AccountService
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject

@Inject
class UpdateEmailUseCase(private val accountService: AccountService) {

    suspend operator fun invoke(email: String): EmptyResource {
        return accountService.updateEmail(email)
    }
}