package com.denchic45.studiversity.feature.course.work.usecase

import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.UUID

class FindCourseWorkUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseElementRepository: CourseElementRepository
) {

    operator fun invoke(workId:UUID) = transactionWorker {
        courseElementRepository.findWorkById(workId) ?: throw NotFoundException()
    }
}