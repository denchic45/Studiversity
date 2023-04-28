package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
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
        uri: Uri
    ): Resource<AttachmentHeader> {
        return courseElementRepository.addFileToWork(courseId, workId, uri)
    }

    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        url: String
    ): Resource<AttachmentHeader> {
        return courseElementRepository.addLinkToWork(courseId, workId, url)
    }
}