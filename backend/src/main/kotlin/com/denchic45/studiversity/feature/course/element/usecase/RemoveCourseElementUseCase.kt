package com.denchic45.studiversity.feature.course.element.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class RemoveCourseElementUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseElementRepository: CourseElementRepository,
    private val attachmentRepository: AttachmentRepository
) {

    suspend operator fun invoke(courseId: UUID, elementId: UUID) = suspendTransactionWorker.invoke {
        attachmentRepository.removeByResourceId(elementId)
        courseElementRepository.remove(courseId, elementId)
    }
}