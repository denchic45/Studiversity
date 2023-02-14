package com.studiversity.feature.specialty.usecase

import com.studiversity.feature.specialty.SpecialtyRepository
import com.studiversity.transaction.TransactionWorker
import com.studiversity.util.searchable

class SearchSpecialtiesUseCase(
    private val transactionWorker: TransactionWorker,
    private val specialtyRepository: SpecialtyRepository
) {
    operator fun invoke(query: String) = transactionWorker {
        specialtyRepository.find(query.searchable())
    }
}