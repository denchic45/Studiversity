package com.denchic45.studiversity.feature.specialty.usecase

import com.denchic45.studiversity.feature.specialty.SpecialtyRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.specialty.model.CreateSpecialtyRequest

class AddSpecialtyUseCase(
    private val transactionWorker: TransactionWorker,
    private val specialtyRepository: SpecialtyRepository
) {
    operator fun invoke(createSpecialtyRequest: CreateSpecialtyRequest) = transactionWorker {
        specialtyRepository.add(createSpecialtyRequest)
    }
}