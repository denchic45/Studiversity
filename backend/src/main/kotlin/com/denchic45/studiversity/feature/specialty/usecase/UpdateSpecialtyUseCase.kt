package com.denchic45.studiversity.feature.specialty.usecase

import com.denchic45.studiversity.feature.specialty.SpecialtyRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.specialty.model.UpdateSpecialtyRequest
import io.ktor.server.plugins.*
import java.util.*

class UpdateSpecialtyUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val specialtyRepository: SpecialtyRepository
) {
  suspend operator fun invoke(id: UUID, updateSpecialtyRequest: UpdateSpecialtyRequest) = suspendTransactionWorker {
        specialtyRepository.update(id, updateSpecialtyRequest) ?: throw NotFoundException()
    }
}