package com.denchic45.studiversity.feature.course.material.usecase

import com.denchic45.studiversity.feature.course.material.CourseMaterialRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.course.material.model.UpdateCourseMaterialRequest
import io.ktor.server.plugins.*
import java.util.*

class UpdateCourseMaterialUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseMaterialRepository: CourseMaterialRepository
) {
    operator fun invoke(courseId:UUID, materialId: UUID, request: UpdateCourseMaterialRequest) = transactionWorker {
        courseMaterialRepository.update(courseId,materialId, request) ?: throw NotFoundException()
    }
}