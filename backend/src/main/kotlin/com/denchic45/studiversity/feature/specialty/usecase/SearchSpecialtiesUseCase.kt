package com.denchic45.studiversity.feature.specialty.usecase

import com.denchic45.studiversity.feature.specialty.SpecialtyRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.studiversity.util.searchable

class SearchSpecialtiesUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val specialtyRepository: SpecialtyRepository
) {
  suspend operator fun invoke(query: String) = suspendTransactionWorker {
        specialtyRepository.find(query.searchable())
    }
}