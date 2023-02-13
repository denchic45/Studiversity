package com.studiversity.feature.specialty.usecase

import com.studiversity.feature.specialty.SpecialtyRepository
import com.studiversity.transaction.TransactionWorker
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