package com.denchic45.studiversity.feature.specialty.usecase

import com.denchic45.studiversity.feature.specialty.SpecialtyRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindSpecialtyByIdUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val specialtyRepository: SpecialtyRepository
) {
  suspend operator fun invoke(id: UUID) = suspendTransactionWorker {
        specialtyRepository.findById(id) ?: throw NotFoundException()
    }
}