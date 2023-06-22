package com.denchic45.studiversity.feature.course.material.usecase

import com.denchic45.studiversity.feature.course.material.CourseMaterialRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.stuiversity.api.course.material.model.CreateCourseMaterialRequest
import io.ktor.server.plugins.*
import java.util.*

class AddCourseMaterialUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseMaterialRepository: CourseMaterialRepository,
) {
    operator fun invoke(courseId: UUID, request: CreateCourseMaterialRequest): CourseMaterialResponse {
        val materialId = transactionWorker { courseMaterialRepository.add(courseId, request) }
        return transactionWorker {
            courseMaterialRepository.findById(materialId)
        } ?: throw NotFoundException()
    }
}