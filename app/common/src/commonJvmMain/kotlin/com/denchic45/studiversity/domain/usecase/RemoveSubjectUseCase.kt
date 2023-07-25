package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubjectRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveSubjectUseCase(
    private val subjectRepository: SubjectRepository,
) {

    suspend operator fun invoke(subjectId: UUID): EmptyResource {
        return subjectRepository.remove(subjectId)
    }
}