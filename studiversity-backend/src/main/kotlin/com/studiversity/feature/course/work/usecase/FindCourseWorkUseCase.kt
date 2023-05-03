package com.studiversity.feature.course.work.usecase

import com.studiversity.feature.course.element.CourseElementRepository
import com.studiversity.transaction.TransactionWorker
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