package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubjectRepository
import com.denchic45.studiversity.domain.Resource
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