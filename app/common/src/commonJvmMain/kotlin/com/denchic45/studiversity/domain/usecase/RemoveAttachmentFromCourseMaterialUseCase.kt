package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.AttachmentRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class RemoveAttachmentFromCourseMaterialUseCase(
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(materialId: UUID, attachmentId: UUID): EmptyResource {
        return attachmentRepository.removeFromCourseMaterial(
            courseMaterialId = materialId,
            attachmentId = attachmentId,
        )
    }
}