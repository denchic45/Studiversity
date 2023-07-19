package com.denchic45.studiversity.feature.studygroup.usecase

import com.denchic45.studiversity.feature.studygroup.repository.StudyGroupRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import io.ktor.server.plugins.*
import java.util.*

class FindStudyGroupByIdUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val studyGroupRepository: StudyGroupRepository
) {
  suspend operator fun invoke(id: UUID): StudyGroupResponse = suspendTransactionWorker {
        studyGroupRepository.findById(id) ?: throw NotFoundException()
    }
}