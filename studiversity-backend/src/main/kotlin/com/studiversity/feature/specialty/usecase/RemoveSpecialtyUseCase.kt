package com.studiversity.feature.specialty.usecase

import com.studiversity.feature.specialty.SpecialtyRepository
import com.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class RemoveSpecialtyUseCase(
    private val transactionWorker: TransactionWorker,
    private val specialtyRepository: SpecialtyRepository
) {
    operator fun invoke(specialtyId: UUID) = transactionWorker {
        if (!specialtyRepository.remove(specialtyId)) throw NotFoundException()
    }
}