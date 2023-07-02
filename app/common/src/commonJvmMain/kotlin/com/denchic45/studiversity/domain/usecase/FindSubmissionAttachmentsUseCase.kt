package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.domain.model.Attachment2
import com.denchic45.studiversity.data.repository.AttachmentRepository
import com.denchic45.studiversity.data.service.DownloadsService
import com.denchic45.studiversity.domain.Resource
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindSubmissionAttachmentsUseCase(
    downloadService: DownloadsService,
    private val attachmentRepository: AttachmentRepository
) : FindAttachmentsUseCase(downloadService) {

    operator fun invoke(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID
    ): Flow<Resource<List<Attachment2>>> {
        return observeAttachments(
            attachmentRepository.observeBySubmission(courseId, courseWorkId, submissionId),
            submissionId
        )
    }
}