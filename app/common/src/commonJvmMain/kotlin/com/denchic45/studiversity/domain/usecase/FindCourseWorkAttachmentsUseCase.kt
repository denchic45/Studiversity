package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.AttachmentRepository
import com.denchic45.studiversity.data.service.DownloadsService
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.ui.model.AttachmentItem
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class FindCourseWorkAttachmentsUseCase(
    private val attachmentRepository: AttachmentRepository,
    downloadService: DownloadsService,
) : FindAttachmentsUseCase(downloadService) {
    operator fun invoke(workId: UUID): Flow<Resource<List<AttachmentItem>>> {
        return observeAttachments(attachmentRepository.observeByCourseWork(workId), workId)
    }
}