package com.studiversity.feature.course.subject.usecase

import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.studiversity.feature.course.subject.SubjectRepository
import com.studiversity.transaction.TransactionWorker
import com.studiversity.util.searchable

class SearchSubjectsUseCase(
    private val transactionWorker: TransactionWorker,
    private val subjectRepository: SubjectRepository
) {
    operator fun invoke(query: String?): List<SubjectResponse> = transactionWorker {
        subjectRepository.find(query?.searchable())
    }
}