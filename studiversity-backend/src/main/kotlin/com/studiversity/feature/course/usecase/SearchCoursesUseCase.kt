package com.studiversity.feature.course.usecase

import com.studiversity.feature.course.repository.CourseRepository
import com.studiversity.transaction.TransactionWorker
import com.studiversity.util.searchable
import java.util.*

class SearchCoursesUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseRepository: CourseRepository
) {
    operator fun invoke(q: String?, memberId: UUID?, subjectId: UUID?) = transactionWorker {
        courseRepository.find(q?.searchable(), memberId, subjectId)
    }
}