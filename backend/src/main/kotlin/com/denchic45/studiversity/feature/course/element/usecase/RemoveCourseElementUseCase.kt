package com.denchic45.studiversity.feature.course.element.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import io.ktor.server.plugins.*
import java.util.*

class RemoveCourseElementUseCase(
    private val transactionWorker: SuspendTransactionWorker,
    private val courseElementRepository: CourseElementRepository,
    private val attachmentRepository: AttachmentRepository
) {

    suspend operator fun invoke(courseId: UUID, elementId: UUID) = transactionWorker.suspendInvoke {
        attachmentRepository.removeByCourseElementId(elementId)
        courseElementRepository.remove(courseId, elementId)
    }
}