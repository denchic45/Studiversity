package com.denchic45.studiversity.feature.user.usecase

import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.studiversity.util.searchable

class SearchUsersUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository
) {
  suspend operator fun invoke(query: String) = suspendTransactionWorker {
        userRepository.find(query.searchable())
    }
}