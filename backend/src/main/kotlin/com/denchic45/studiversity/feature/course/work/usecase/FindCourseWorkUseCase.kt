package com.denchic45.studiversity.feature.course.work.usecase

import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindCourseWorkUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseElementRepository: CourseElementRepository
) {

  suspend operator fun invoke(workId: UUID) = suspendTransactionWorker {
        courseElementRepository.findWorkById(workId) ?: throw NotFoundException()
    }
}