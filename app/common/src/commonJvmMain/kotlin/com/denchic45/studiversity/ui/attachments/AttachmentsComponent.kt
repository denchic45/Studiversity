package com.denchic45.studiversity.ui.attachments

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.model.Attachment2
import com.denchic45.studiversity.domain.model.FileState
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.DownloadFileUseCase
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class AttachmentsComponent(
    private val downloadFileUseCase: DownloadFileUseCase,
    @Assisted
    attachments: Flow<Resource<List<Attachment2>>>,
    @Assisted
    private val onAddAttachment: ((AttachmentRequest) -> Unit)?,
    @Assisted
    private val onRemoveAttachment: ((UUID) -> Unit)?,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()
    val attachments = attachments.stateInResource(componentScope)

    val openAttachment = MutableSharedFlow<AttachmentItem>()

    fun onAttachmentClick(item: AttachmentItem) {
        when (item) {
            is AttachmentItem.FileAttachmentItem -> when (item.state) {
                FileState.Downloaded -> componentScope.launch { openAttachment.emit(item) }
                FileState.Preview, FileState.FailDownload -> componentScope.launch {
                    downloadFileUseCase(item.attachmentId)
                }

                else -> {}
            }

            is AttachmentItem.LinkAttachmentItem -> componentScope.launch {
                openAttachment.emit(item)
            }
        }
    }

    fun onRemoveClick(attachmentId: UUID) {
        onRemoveAttachment?.invoke(attachmentId)
    }

    fun onAddAttachmentClick(request: AttachmentRequest) {
        onAddAttachment?.invoke(request)
    }
}