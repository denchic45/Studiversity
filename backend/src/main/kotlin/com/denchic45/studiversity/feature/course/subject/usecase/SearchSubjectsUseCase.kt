package com.denchic45.studiversity.feature.course.subject.usecase

import com.denchic45.studiversity.feature.course.subject.SubjectRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.studiversity.util.searchable
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse

class SearchSubjectsUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val subjectRepository: SubjectRepository
) {
    suspend operator fun invoke(query: String?): List<SubjectResponse> = suspendTransactionWorker {
        subjectRepository.find(query?.searchable())
    }
}