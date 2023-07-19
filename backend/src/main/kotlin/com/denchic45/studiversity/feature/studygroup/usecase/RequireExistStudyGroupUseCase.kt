package com.denchic45.studiversity.feature.studygroup.usecase

import com.denchic45.studiversity.feature.studygroup.repository.StudyGroupRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class RequireExistStudyGroupUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val studyGroupRepository: StudyGroupRepository
) {

  suspend operator fun invoke(id: UUID) = suspendTransactionWorker {
        if (!studyGroupRepository.exist(id)) throw NotFoundException()
    }
}