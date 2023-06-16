package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupRepository
import com.denchic45.studiversity.domain.EmptyResource
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class AttachStudyGroupsToCourseUseCase(
    private val studyGroupRepository: StudyGroupRepository
) {

    operator fun invoke(courseId: UUID,studyGroupId:UUID): Flow<EmptyResource> {
        return studyGroupRepository.addToCourse(courseId, studyGroupId)
    }
}