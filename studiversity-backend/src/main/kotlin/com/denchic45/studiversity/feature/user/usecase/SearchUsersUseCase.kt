package com.denchic45.studiversity.feature.user.usecase

import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.studiversity.util.searchable

class SearchUsersUseCase(
    private val transactionWorker: TransactionWorker,
    private val userRepository: UserRepository
) {
    operator fun invoke(query: String) = transactionWorker {
        userRepository.find(query.searchable())
    }
}