package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.AttachmentRepository
import com.denchic45.studiversity.data.service.DownloadsService
import com.denchic45.studiversity.domain.model.Attachment2
import com.denchic45.studiversity.domain.resource.Resource
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindCourseWorkAttachmentsUseCase(
    private val attachmentRepository: AttachmentRepository,
    downloadService: DownloadsService,
) : FindAttachmentsUseCase(downloadService) {
    operator fun invoke(
        courseId: UUID,
        workId: UUID,
    ): Flow<Resource<List<Attachment2>>> {
        return observeAttachments(
            attachmentRepository.observeByCourseWork(courseId, workId),
            workId
        )
    }
}