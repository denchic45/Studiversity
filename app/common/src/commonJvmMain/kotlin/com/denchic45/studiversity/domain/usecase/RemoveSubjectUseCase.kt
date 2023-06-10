package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubjectRepository
import com.denchic45.studiversity.domain.EmptyResource
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