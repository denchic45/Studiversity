package com.denchic45.studiversity.feature.studygroup.usecase

import com.denchic45.studiversity.feature.studygroup.repository.StudyGroupRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.studiversity.util.searchable
import java.util.*

class SearchStudyGroupsUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val studyGroupRepository: StudyGroupRepository
) {
  suspend operator fun invoke(
        query: String?,
        memberId: UUID?,
        roleId: Long?,
        specialtyId: UUID?,
        academicYear: Int?
    ) = suspendTransactionWorker {
        studyGroupRepository.find(query?.searchable(), memberId, roleId, specialtyId, academicYear)
    }
}