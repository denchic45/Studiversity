package com.studiversity.feature.studygroup.usecase

import com.studiversity.feature.studygroup.StudyGroupErrors
import com.stuiversity.api.studygroup.model.UpdateStudyGroupRequest
import com.studiversity.feature.studygroup.repository.StudyGroupRepository
import io.ktor.server.plugins.*
import java.util.*

class UpdateStudyGroupUseCase(private val groupRepository: StudyGroupRepository) {
    operator fun invoke(id: UUID, request: UpdateStudyGroupRequest) {
        groupRepository.update(id, request).apply {
            if (!this)
                throw NotFoundException(StudyGroupErrors.GROUP_DOES_NOT_EXIST)
        }
    }
}