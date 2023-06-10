package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubjectRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindSubjectByIdUseCase @Inject constructor(
    private val subjectRepository: SubjectRepository,
) {
    operator fun invoke(courseId: UUID): Flow<Resource<SubjectResponse>> {
        return subjectRepository.findById(courseId)
    }
}
