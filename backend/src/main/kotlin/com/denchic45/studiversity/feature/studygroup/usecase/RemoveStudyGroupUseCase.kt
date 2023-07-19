package com.denchic45.studiversity.feature.studygroup.usecase

import com.denchic45.studiversity.feature.role.repository.ScopeRepository
import com.denchic45.studiversity.feature.studygroup.StudyGroupErrors
import com.denchic45.studiversity.feature.studygroup.repository.StudyGroupRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class RemoveStudyGroupUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val groupRepository: StudyGroupRepository,
    private val scopeRepository: ScopeRepository
) {
  suspend operator fun invoke(id: UUID) = suspendTransactionWorker {
        groupRepository.remove(id).apply {
            if (!this)
                throw NotFoundException(StudyGroupErrors.GROUP_DOES_NOT_EXIST)
            scopeRepository.remove(id)
        }
    }
}