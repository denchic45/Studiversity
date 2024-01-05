package com.denchic45.studiversity.feature.course.usecase

import com.denchic45.studiversity.feature.course.repository.CourseRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.studiversity.util.searchable
import java.util.*

class SearchCoursesUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(
        memberId: UUID?,
        studyGroupId: UUID?,
        subjectId: UUID?,
        archived: Boolean?,
        q: String?
    ) = suspendTransactionWorker {
        courseRepository.find(memberId, studyGroupId, subjectId, archived, q?.searchable())
    }
}