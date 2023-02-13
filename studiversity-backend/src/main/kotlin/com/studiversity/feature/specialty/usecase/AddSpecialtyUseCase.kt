package com.studiversity.feature.specialty.usecase

import com.studiversity.feature.specialty.SpecialtyRepository
import com.studiversity.transaction.TransactionWorker
import com.stuiversity.api.specialty.model.CreateSpecialtyRequest

class AddSpecialtyUseCase(
    private val transactionWorker: TransactionWorker,
    private val specialtyRepository: SpecialtyRepository
) {
    operator fun invoke(createSpecialtyRequest: CreateSpecialtyRequest) = transactionWorker {
        specialtyRepository.add(createSpecialtyRequest)
    }
}