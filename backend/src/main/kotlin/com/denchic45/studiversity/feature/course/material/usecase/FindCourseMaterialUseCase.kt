package com.denchic45.studiversity.feature.course.material.usecase

import com.denchic45.studiversity.feature.course.material.CourseMaterialRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindCourseMaterialUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseMaterialRepository: CourseMaterialRepository
) {

    operator fun invoke(workId: UUID) = transactionWorker {
        courseMaterialRepository.findById(workId) ?: throw NotFoundException()
    }
}