package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.domain.model.Attachment2
import com.denchic45.studiversity.data.repository.AttachmentRepository
import com.denchic45.studiversity.data.service.DownloadsService
import com.denchic45.studiversity.domain.Resource
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
        return observeAttachments(attachmentRepository.observeByCourseWork(courseId, workId), workId)
    }
}