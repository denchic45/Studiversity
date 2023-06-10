package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import com.eygraber.uri.Uri
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class UploadAttachmentToCourseWorkUseCase @javax.inject.Inject constructor(
    private val courseElementRepository: CourseElementRepository
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        request: AttachmentRequest
    ): Resource<AttachmentHeader> {
        return courseElementRepository.addAttachmentToWork(courseId, workId, request)
    }
}