package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.domain.model.Attachment2
import com.denchic45.kts.data.repository.AttachmentRepository
import com.denchic45.kts.data.service.DownloadsService
import com.denchic45.kts.domain.Resource
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindCourseWorkAttachmentsUseCase @Inject constructor(
    private val attachmentRepository: AttachmentRepository,
    downloadService: DownloadsService,
) : FindAttachmentsUseCase(downloadService) {
    operator fun invoke(
        courseId: UUID,
        workId: UUID,
    ): Flow<Resource<List<Attachment2>>> {
        return observeAttachments(attachmentRepository.observeByCourseWork(courseId, workId))
    }
}