package com.denchic45.studiversity.ui.attachments

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.usecase.FindAttachmentsByResourceUseCase
import com.denchic45.studiversity.domain.usecase.RemoveAttachmentUseCase
import com.denchic45.studiversity.domain.usecase.UploadAttachmentToResourceUseCase
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class AttachmentsComponent(
    private val findAttachmentsByResourceUseCase: FindAttachmentsByResourceUseCase,
    private val uploadAttachmentToResourceUseCase: UploadAttachmentToResourceUseCase,
    private val removeAttachmentUseCase: RemoveAttachmentUseCase,
    @Assisted
    private val resourceType: String,
    @Assisted
    private val resourceId: UUID,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()
    val attachments = findAttachmentsByResourceUseCase(resourceType, resourceId)
        .shareIn(componentScope, SharingStarted.Lazily)

    suspend fun uploadAttachmentByResource(attachmentRequest: AttachmentRequest): Resource<AttachmentHeader> {
        return uploadAttachmentToResourceUseCase(resourceType, resourceId, attachmentRequest)
    }

    suspend fun removeAttachment(attachmentId: UUID) {
        removeAttachmentUseCase(attachmentId)
    }
}