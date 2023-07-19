package com.denchic45.studiversity.feature.course.work.usecase

import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class FindCourseWorksUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseElementRepository: CourseElementRepository
) {
  suspend operator fun invoke(authorId: UUID, late: Boolean?, submitted: Boolean?) = suspendTransactionWorker {
        courseElementRepository.findByAuthor(authorId, late, submitted)
    }
}