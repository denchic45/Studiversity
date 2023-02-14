package com.studiversity.feature.course.subject.usecase

import com.studiversity.feature.course.subject.SubjectRepository
import com.studiversity.transaction.TransactionWorker
import com.studiversity.util.searchable
import com.stuiversity.api.course.subject.model.SubjectResponse

class SearchSubjectsUseCase(
    private val transactionWorker: TransactionWorker,
    private val subjectRepository: SubjectRepository
) {
    operator fun invoke(query: String?): List<SubjectResponse> = transactionWorker {
        query?.let { subjectRepository.find(it.searchable()) } ?: subjectRepository.findAll()
    }
}