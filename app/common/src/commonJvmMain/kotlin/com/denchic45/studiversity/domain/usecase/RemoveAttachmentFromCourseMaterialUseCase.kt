package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.AttachmentRepository
import com.denchic45.studiversity.domain.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveAttachmentFromCourseMaterialUseCase @javax.inject.Inject constructor(
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(
        courseId: UUID,
        materialId: UUID,
        attachmentId: UUID
    ): EmptyResource {
        return attachmentRepository.removeFromCourseMaterial(
            attachmentId = attachmentId,
            courseId = courseId,
            materialId = materialId
        )
    }
}