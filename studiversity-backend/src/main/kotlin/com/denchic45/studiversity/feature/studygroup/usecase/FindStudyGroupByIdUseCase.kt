package com.denchic45.studiversity.feature.studygroup.usecase

import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.studiversity.feature.studygroup.repository.StudyGroupRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindStudyGroupByIdUseCase(
    private val transactionWorker: TransactionWorker,
    private val studyGroupRepository: StudyGroupRepository
) {
    operator fun invoke(id: UUID): StudyGroupResponse = transactionWorker {
        studyGroupRepository.findById(id) ?: throw NotFoundException()
    }
}