package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class DetachStudyGroupsToCourseUseCase(
    private val studyGroupRepository: StudyGroupRepository
) {

    suspend operator fun invoke(courseId: UUID, studyGroupId: UUID): EmptyResource {
        return studyGroupRepository.removeFromCourse(studyGroupId, courseId)
    }
}