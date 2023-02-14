package com.studiversity.feature.studygroup.usecase

import com.studiversity.feature.studygroup.repository.StudyGroupRepository
import com.studiversity.transaction.TransactionWorker
import com.studiversity.util.searchable

class SearchStudyGroupsUseCase(
    private val transactionWorker: TransactionWorker,
    private val studyGroupRepository: StudyGroupRepository
) {
    operator fun invoke(query: String) = transactionWorker {
        studyGroupRepository.find(query.searchable())
    }
}