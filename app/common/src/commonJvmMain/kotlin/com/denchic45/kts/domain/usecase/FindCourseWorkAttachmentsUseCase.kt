package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.domain.model.Attachment2
import com.denchic45.kts.data.domain.model.FileAttachment2
import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.data.domain.model.LinkAttachment2
import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.kts.data.service.DownloadsService
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.flatMapResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class FindCourseWorkAttachmentsUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
    private val downloadService: DownloadsService,
) {
     operator fun invoke(
        courseId: UUID,
        workId: UUID,
    ): Flow<Resource<List<Attachment2>>> {
        return courseElementRepository.findAttachments(courseId, workId)
            .flatMapResource { attachments ->
                downloadingFilesFlow(
                    attachments.filterIsInstance<FileAttachment2>()
                        .filter { it.state == FileState.Preview }
                ).map { states ->
                    Resource.Success(
                        attachments.map { attachment ->
                            when (attachment) {
                                is FileAttachment2 -> if (attachment.state == FileState.Preview)
                                    attachment.copy(state = states.getValue(attachment.id))
                                else attachment
                                is LinkAttachment2 -> attachment
                            }
                        }
                    )
                }
            }
    }

    private fun downloadingFilesFlow(list: List<FileAttachment2>): Flow<Map<UUID, FileState>> {
        return combine(list.map { attachment ->
            val attachmentId = attachment.id
            downloadService.getDownloading(attachmentId).map { state -> attachmentId to state }
        }) { it.toMap() }
    }

}