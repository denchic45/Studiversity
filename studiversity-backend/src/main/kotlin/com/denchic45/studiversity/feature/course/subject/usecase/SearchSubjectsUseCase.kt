package com.denchic45.studiversity.feature.course.subject.usecase

import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.studiversity.feature.course.subject.SubjectRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.studiversity.util.searchable

class SearchSubjectsUseCase(
    private val transactionWorker: TransactionWorker,
    private val subjectRepository: SubjectRepository
) {
    operator fun invoke(query: String?): List<SubjectResponse> = transactionWorker {
        subjectRepository.find(query?.searchable())
    }
}