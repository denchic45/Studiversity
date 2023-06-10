package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class RemoveAttachmentFromCourseWorkUseCase @javax.inject.Inject constructor(
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