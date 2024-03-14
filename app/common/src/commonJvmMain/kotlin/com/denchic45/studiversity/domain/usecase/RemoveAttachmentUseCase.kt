package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.AttachmentRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveAttachmentUseCase(
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(
        attachmentId: UUID
    ): EmptyResource {
        return attachmentRepository.removeById(attachmentId)
    }
}