package com.denchic45.studiversity.feature.course.material.usecase

import com.denchic45.studiversity.feature.course.material.CourseMaterialRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindCourseMaterialUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseMaterialRepository: CourseMaterialRepository
) {

    suspend operator fun invoke(materialId: UUID) = suspendTransactionWorker {
        courseMaterialRepository.findById(materialId) ?: throw NotFoundException()
    }
}