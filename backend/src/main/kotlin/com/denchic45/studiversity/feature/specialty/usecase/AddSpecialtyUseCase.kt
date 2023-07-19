package com.denchic45.studiversity.feature.specialty.usecase

import com.denchic45.studiversity.feature.specialty.SpecialtyRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.specialty.model.CreateSpecialtyRequest

class AddSpecialtyUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val specialtyRepository: SpecialtyRepository
) {
  suspend operator fun invoke(createSpecialtyRequest: CreateSpecialtyRequest) = suspendTransactionWorker {
        specialtyRepository.add(createSpecialtyRequest)
    }
}