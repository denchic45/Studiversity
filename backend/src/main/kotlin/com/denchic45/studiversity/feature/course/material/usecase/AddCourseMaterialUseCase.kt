package com.denchic45.studiversity.feature.course.material.usecase

import com.denchic45.studiversity.feature.course.material.CourseMaterialRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.stuiversity.api.course.material.model.CreateCourseMaterialRequest
import io.ktor.server.plugins.*
import java.util.*

class AddCourseMaterialUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseMaterialRepository: CourseMaterialRepository,
) {
    suspend operator fun invoke(courseId: UUID, request: CreateCourseMaterialRequest): CourseMaterialResponse {
        val materialId = suspendTransactionWorker { courseMaterialRepository.add(courseId, request) }
        return suspendTransactionWorker {
            courseMaterialRepository.findById(materialId)
        } ?: throw NotFoundException()
    }
}