package com.denchic45.kts.ui.coursework.details

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.DownloadFileUseCase
import com.denchic45.kts.domain.usecase.FindCourseWorkAttachmentsUseCase
import com.denchic45.kts.domain.usecase.FindCourseWorkUseCase
import com.denchic45.kts.ui.model.AttachmentItem
import com.denchic45.kts.ui.model.toAttachmentItems
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseWorkDetailsComponent(
    private val findCourseWorkUseCase: FindCourseWorkUseCase,
    private val findCourseWorkAttachmentsUseCase: FindCourseWorkAttachmentsUseCase,
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
        when (item) {
            is AttachmentItem.FileAttachmentItem -> when (item.state) {
                FileState.Downloaded -> componentScope.launch { openAttachment.emit(item) }
                FileState.Preview -> componentScope.launch {
                    downloadFileUseCase(item.attachmentId!!)
                    // TODO: Возможно использовать в будущем: открывать файл сразу после его загрузки
//                            .collect {
//                            if (it == FileState.Downloaded)
//                                openAttachment.postValue(item.path.toFile())
//                        }
                }

                else -> {}
            }

            is AttachmentItem.LinkAttachmentItem -> componentScope.launch {
                openAttachment.emit(item)
            }
        }
    }
}