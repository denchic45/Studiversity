package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.kts.domain.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class RemoveAttachmentFromCourseWorkUseCase(
    private val courseElementRepository: CourseElementRepository
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        attachmentId:UUID
    ): EmptyResource {
        return courseElementRepository.removeAttachmentFromWork(courseId, workId, attachmentId)
    }
}