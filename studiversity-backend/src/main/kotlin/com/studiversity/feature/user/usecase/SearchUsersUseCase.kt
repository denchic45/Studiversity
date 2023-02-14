package com.studiversity.feature.user.usecase

import com.studiversity.feature.user.UserRepository
import com.studiversity.transaction.TransactionWorker
import com.studiversity.util.searchable

class SearchUsersUseCase(
    private val transactionWorker: TransactionWorker,
    private val userRepository: UserRepository
) {
    operator fun invoke(query: String) = transactionWorker {
        userRepository.find(query.searchable())
    }
}