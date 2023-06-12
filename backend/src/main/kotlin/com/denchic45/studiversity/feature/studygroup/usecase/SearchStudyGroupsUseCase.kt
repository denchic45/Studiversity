package com.denchic45.studiversity.feature.studygroup.usecase

import com.denchic45.studiversity.feature.studygroup.repository.StudyGroupRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.studiversity.util.searchable
import java.util.*

class SearchStudyGroupsUseCase(
    private val transactionWorker: TransactionWorker,
    private val studyGroupRepository: StudyGroupRepository
) {
    operator fun invoke(
        query: String?,
        memberId: UUID?,
        roleId: Long?,
        specialtyId: UUID?,
        academicYear: Int?
    ): List<Any> = transactionWorker {
        studyGroupRepository.find(query?.searchable(), memberId, roleId, specialtyId, academicYear)
    }
}