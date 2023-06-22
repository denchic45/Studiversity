package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.AttachmentRepository
import com.denchic45.studiversity.domain.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveAttachmentFromCourseWorkUseCase @javax.inject.Inject constructor(
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        attachmentId: UUID
    ): EmptyResource {
        return attachmentRepository.removeFromCourseWork(
            attachmentId = attachmentId,
            courseId = courseId,
            workId = workId
        )
    }
}