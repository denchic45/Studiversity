package com.denchic45.studiversity.ui.attachments

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.model.Attachment2
import com.denchic45.studiversity.domain.model.FileState
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.usecase.DownloadFileUseCase
import com.denchic45.studiversity.domain.usecase.FindAttachmentsByResourceUseCase
import com.denchic45.studiversity.domain.usecase.UploadAttachmentToResourceUseCase
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class AttachmentsComponent(
    private val downloadFileUseCase: DownloadFileUseCase,
    private val findAttachmentsByResourceUseCase: FindAttachmentsByResourceUseCase,
    private val uploadAttachmentToResourceUseCase: UploadAttachmentToResourceUseCase,

//    @Assisted
//    observedAttachments: Flow<Attachment2>,
//    @Assisted
//    onAddAttachment: (AttachmentRequest) -> Unit,
//    @Assisted
//    onRemoveAttachment: (UUID) -> Unit,

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

    fun onAttachmentClick(item: AttachmentItem) {
//        when (item) {
//            is AttachmentItem.FileAttachmentItem -> when (item.state) {
//                FileState.Downloaded -> componentScope.launch { openAttachment.emit(item) }
//                FileState.Preview, FileState.FailDownload -> componentScope.launch {
//                    downloadFileUseCase(item.attachmentId)
//                }
//
//                else -> {}
//            }
//
//            is AttachmentItem.LinkAttachmentItem -> componentScope.launch {
//                openAttachment.emit(item)
//            }
//        }
    }

    suspend fun uploadAttachmentByResource(attachmentRequest: AttachmentRequest): Resource<AttachmentHeader> {
        return uploadAttachmentToResourceUseCase(resourceType, resourceId, attachmentRequest)
    }

    suspend fun removeAttachment(attachmentId: UUID) {
//        removeAttachmentUseCase(attachmentId)
    }
}