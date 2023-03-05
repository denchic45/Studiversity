package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.domain.model.Attachment2
import com.denchic45.kts.data.repository.AttachmentRepository
import com.denchic45.kts.data.service.DownloadsService
import com.denchic45.kts.domain.Resource
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.*

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
            attachmentRepository.observeBySubmission(courseId, courseWorkId, submissionId)
        )
    }
}