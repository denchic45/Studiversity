package com.denchic45.studiversity.ui.coursework.details

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.model.FileState
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.DownloadFileUseCase
import com.denchic45.studiversity.domain.usecase.FindCourseWorkAttachmentsUseCase
import com.denchic45.studiversity.domain.usecase.FindCourseWorkUseCase
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.ui.model.toAttachmentItems
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseWorkDetailsComponent(
    private val findCourseWorkUseCase: FindCourseWorkUseCase,
    findCourseWorkAttachmentsUseCase: FindCourseWorkAttachmentsUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val elementId: UUID,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val courseWork = flow { emit(findCourseWorkUseCase(courseId, elementId)) }
        .stateInResource(componentScope, SharingStarted.Eagerly)
    val attachments = findCourseWorkAttachmentsUseCase(courseId, elementId)
        .mapResource { it.toAttachmentItems() }
        .stateInResource(componentScope, SharingStarted.Eagerly)

    val openAttachment = MutableSharedFlow<AttachmentItem>()

    fun onAttachmentClick(item: AttachmentItem) {
        componentScope.launch {
            when (item) {
                is AttachmentItem.FileAttachmentItem -> when (item.state) {
                    FileState.Downloaded -> openAttachment.emit(item)
                    FileState.FailDownload, FileState.Preview -> downloadFileUseCase(item.attachmentId)
                    else -> {}
                }

                is AttachmentItem.LinkAttachmentItem -> openAttachment.emit(item)
            }
        }
    }
}