package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import java.util.UUID
import javax.inject.Inject

class FindCourseWorkAttachmentsUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(courseId: UUID,workId:UUID): Resource<List<AttachmentHeader>> {
        return courseElementRepository.findAttachments(courseId, workId)
    }
}