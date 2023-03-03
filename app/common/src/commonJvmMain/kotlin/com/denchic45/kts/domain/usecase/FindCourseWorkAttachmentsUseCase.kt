package com.denchic45.kts.domain.usecase

import com.denchic45.kts.AttachmentEntity
import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.kts.data.service.DownloadsService
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.flatMapResource
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class FindCourseWorkAttachmentsUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
    private val downloadService: DownloadsService
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID
    ): Flow<Resource<List<AttachmentEntity>>> {
        return courseElementRepository.findAttachments(courseId, workId).flatMapResource { list ->
            TODO("map only file attachments to download observable attachments")
        }
    }
}