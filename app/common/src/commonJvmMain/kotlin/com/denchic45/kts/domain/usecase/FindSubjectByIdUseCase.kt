package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SubjectRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindSubjectByIdUseCase @Inject constructor(
    private val subjectRepository: SubjectRepository,
) {
    suspend operator fun invoke(courseId: UUID): Resource<SubjectResponse> {
        return subjectRepository.findById(courseId)
    }
}
