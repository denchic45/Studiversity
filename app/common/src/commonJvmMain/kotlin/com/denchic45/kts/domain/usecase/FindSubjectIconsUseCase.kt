package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SubjectRepository
import com.denchic45.kts.domain.Resource
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class FindSubjectIconsUseCase(
    private val subjectRepository: SubjectRepository
) {
    operator fun invoke(): Flow<Resource<List<String>>> {
        return subjectRepository.findIcons()
    }
}