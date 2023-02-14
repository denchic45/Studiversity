package com.studiversity.feature.course.usecase

import com.studiversity.feature.course.repository.CourseRepository
import com.studiversity.transaction.TransactionWorker
import com.studiversity.util.searchable

class SearchCoursesUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseRepository: CourseRepository
) {
    operator fun invoke(query: String) = transactionWorker {
        courseRepository.find(query.searchable())
    }
}