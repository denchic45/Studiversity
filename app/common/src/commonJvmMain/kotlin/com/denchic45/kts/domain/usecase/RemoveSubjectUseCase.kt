package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SubjectRepository
import com.denchic45.kts.domain.EmptyResource
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class RemoveSubjectUseCase @Inject constructor(
    private val subjectRepository: SubjectRepository,
) {

    suspend operator fun invoke(subjectId: UUID): EmptyResource {
        return subjectRepository.remove(subjectId)
    }
}