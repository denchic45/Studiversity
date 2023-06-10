package com.denchic45.studiversity.feature.specialty.usecase

import com.denchic45.studiversity.feature.specialty.SpecialtyRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.studiversity.util.searchable

class SearchSpecialtiesUseCase(
    private val transactionWorker: TransactionWorker,
    private val specialtyRepository: SpecialtyRepository
) {
    operator fun invoke(query: String) = transactionWorker {
        specialtyRepository.find(query.searchable())
    }
}