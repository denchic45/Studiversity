package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubjectRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindSubjectByIdUseCase(
    private val subjectRepository: SubjectRepository,
) {
    operator fun invoke(courseId: UUID): Flow<Resource<SubjectResponse>> {
        return subjectRepository.findById(courseId)
    }
}
