package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseWorkRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UploadAttachmentToCourseWorkUseCase(
    private val courseWorkRepository: CourseWorkRepository
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        request: AttachmentRequest
    ): Resource<AttachmentHeader> {
        return courseWorkRepository.addAttachment(courseId, workId, request)
    }
}