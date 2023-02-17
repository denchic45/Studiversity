package com.studiversity.feature.specialty.usecase

import com.studiversity.feature.specialty.SpecialtyRepository
import com.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.specialty.model.UpdateSpecialtyRequest
import io.ktor.server.plugins.*
import java.util.*

class UpdateSpecialtyUseCase(
    private val transactionWorker: TransactionWorker,
    private val specialtyRepository: SpecialtyRepository
) {
    operator fun invoke(id: UUID, updateSpecialtyRequest: UpdateSpecialtyRequest) = transactionWorker {
        specialtyRepository.update(id, updateSpecialtyRequest) ?: throw NotFoundException()
    }
}