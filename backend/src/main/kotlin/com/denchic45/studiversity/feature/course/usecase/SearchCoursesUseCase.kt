package com.denchic45.studiversity.feature.course.usecase

import com.denchic45.studiversity.feature.course.repository.CourseRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.studiversity.util.searchable
import java.util.*

class SearchCoursesUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseRepository: CourseRepository
) {
    operator fun invoke(memberId: UUID?, studyGroupId: UUID?, subjectId: UUID?, archived: Boolean?, q: String?) =
        transactionWorker {
            courseRepository.find(memberId, studyGroupId, subjectId, archived, q?.searchable())
        }
}