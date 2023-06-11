package com.denchic45.studiversity.feature.specialty.usecase

import com.denchic45.studiversity.feature.specialty.SpecialtyRepository
import com.denchic45.studiversity.transaction.TransactionWorker
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