package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupRepository
import com.denchic45.stuiversity.util.UUIDWrapper
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindStudyGroupsUseCase(private val studyGroupRepository: StudyGroupRepository) {
    operator fun invoke(
        memberId: UUIDWrapper? = null,
        roleId: Long? = null,
        specialtyId: UUID? = null,
        academicYear: Int? = null,
        query: String? = null,
    ) = studyGroupRepository.findBy(memberId, roleId, specialtyId, academicYear, query)
}