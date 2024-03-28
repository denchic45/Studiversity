package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.api.account.model.AccountEmailResponse
import com.denchic45.studiversity.data.service.AccountService
import com.denchic45.studiversity.domain.resource.Resource
import me.tatarka.inject.annotations.Inject

@Inject
class FindAccountEmailUseCase(private val accountService: AccountService) {
    suspend operator fun invoke(): Resource<AccountEmailResponse> {
        return accountService.findEmail()
    }
}