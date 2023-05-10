package com.denchic45.kts.ui.coursework.submissiondetails

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.CancelGradeSubmissionUseCase
import com.denchic45.kts.domain.usecase.DownloadFileUseCase
import com.denchic45.kts.domain.usecase.FindSubmissionAttachmentsUseCase
import com.denchic45.kts.domain.usecase.FindSubmissionByIdUseCase
import com.denchic45.kts.domain.usecase.GradeSubmissionUseCase
import com.denchic45.kts.ui.coursework.toUiState
import com.denchic45.kts.ui.model.AttachmentItem
import com.denchic45.kts.ui.model.toAttachmentItems
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID


@Inject
class SubmissionDetailsComponent(
    private val downloadFileUseCase: DownloadFileUseCase,
    private val findSubmissionByIdUseCase: FindSubmissionByIdUseCase,
    findSubmissionAttachmentsUseCase: FindSubmissionAttachmentsUseCase,
    private val gradeSubmissionUseCase: GradeSubmissionUseCase,
    private val cancelGradeSubmissionUseCase: CancelGradeSubmissionUseCase,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val workId: UUID,
    @Assisted
    private val submissionId: UUID,
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val submission = flow {
        emit(findSubmissionByIdUseCase(courseId, workId, submissionId))
    }.shareIn(componentScope, SharingStarted.Lazily)

    private val attachments = findSubmissionAttachmentsUseCase(courseId, workId, submissionId)
        .shareIn(componentScope, SharingStarted.Lazily)

    val uiState = combine(submission, attachments) { submissionRes, attachmentsRes ->
        submissionRes.mapResource { submission ->
            attachmentsRes.map { attachments ->
                submission.toUiState(attachments.toAttachmentItems())
            }
        }
    }.stateInResource(componentScope)

    val openAttachment = MutableSharedFlow<AttachmentItem>()

    fun onAttachmentClick(item: AttachmentItem) {
        when (item) {
            is AttachmentItem.FileAttachmentItem -> when (item.state) {
                FileState.Downloaded -> componentScope.launch { openAttachment.emit(item) }
                FileState.Preview, FileState.FailDownload -> componentScope.launch {
                    downloadFileUseCase(item.attachmentId!!)
                }

                else -> {}
            }

            is AttachmentItem.LinkAttachmentItem -> componentScope.launch {
                openAttachment.emit(item)
            }
        }
    }

    fun onGrade(value: Int) {
        componentScope.launch {
            gradeSubmissionUseCase(courseId, workId, submissionId, value)
        }
    }

    fun onGradeCancel() {
        componentScope.launch {
            cancelGradeSubmissionUseCase(courseId, workId, submissionId)
        }
    }
}