package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.AttachmentRepository
import com.denchic45.studiversity.data.service.DownloadsService
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.ui.model.AttachmentItem
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class FindCourseMaterialAttachmentsUseCase(
    private val attachmentRepository: AttachmentRepository,
    downloadService: DownloadsService,
) : FindAttachmentsUseCase(downloadService) {
    operator fun invoke(materialId: UUID): Flow<Resource<List<AttachmentItem>>> {
        return observeAttachments(attachmentRepository.observeByCourseMaterial(materialId), materialId)
    }
}