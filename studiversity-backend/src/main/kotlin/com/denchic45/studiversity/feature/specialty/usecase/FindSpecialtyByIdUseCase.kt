package com.denchic45.studiversity.feature.specialty.usecase

import com.denchic45.studiversity.feature.specialty.SpecialtyRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindSpecialtyByIdUseCase(
    private val transactionWorker: TransactionWorker,
    private val specialtyRepository: SpecialtyRepository
) {
    operator fun invoke(id: UUID) = transactionWorker {
        specialtyRepository.findById(id) ?: throw NotFoundException()
    }
}