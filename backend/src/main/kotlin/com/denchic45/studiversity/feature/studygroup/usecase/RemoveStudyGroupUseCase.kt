package com.denchic45.studiversity.feature.studygroup.usecase

import com.denchic45.studiversity.feature.role.repository.ScopeRepository
import com.denchic45.studiversity.feature.studygroup.StudyGroupErrors
import com.denchic45.studiversity.feature.studygroup.repository.StudyGroupRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class RemoveStudyGroupUseCase(
    private val transactionWorker: TransactionWorker,
    private val groupRepository: StudyGroupRepository,
    private val scopeRepository: ScopeRepository
) {
    operator fun invoke(id: UUID) = transactionWorker {
        groupRepository.remove(id).apply {
            if (!this)
                throw NotFoundException(StudyGroupErrors.GROUP_DOES_NOT_EXIST)
            scopeRepository.remove(id)
        }
    }
}