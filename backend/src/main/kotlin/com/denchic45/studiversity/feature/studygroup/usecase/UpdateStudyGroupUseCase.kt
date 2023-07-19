package com.denchic45.studiversity.feature.studygroup.usecase

import com.denchic45.stuiversity.api.studygroup.model.UpdateStudyGroupRequest
import com.denchic45.studiversity.feature.studygroup.StudyGroupErrors
import com.denchic45.studiversity.feature.studygroup.repository.StudyGroupRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class UpdateStudyGroupUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val studyGroupRepository: StudyGroupRepository
) {
  suspend operator fun invoke(id: UUID, request: UpdateStudyGroupRequest) = suspendTransactionWorker {
        studyGroupRepository.update(id, request)
            .takeIf { it != null } ?: throw NotFoundException(StudyGroupErrors.GROUP_DOES_NOT_EXIST)
    }
}