package com.denchic45.studiversity.ui.coursework.submissiondetails

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.model.FileState
import com.denchic45.studiversity.domain.resource.*
import com.denchic45.studiversity.domain.usecase.*
import com.denchic45.studiversity.ui.coursework.SubmissionUiState
import com.denchic45.studiversity.ui.coursework.toUiState
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.ui.model.toAttachmentItems
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*


@Inject
class SubmissionDetailsComponent(
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
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
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val capabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = courseId, capabilities = listOf(Capability.GradeSubmission)
    ).shareIn(componentScope, SharingStarted.Lazily)

    private val allowGradeSubmission = capabilities.mapResource {
        it.hasCapability(Capability.GradeSubmission)
    }.stateInResource(componentScope)

    private val submission = flow {
        emit(findSubmissionByIdUseCase(submissionId))
    }.shareIn(componentScope, SharingStarted.Lazily, 1)

    private val attachments = findSubmissionAttachmentsUseCase(submissionId)
        .shareIn(componentScope, SharingStarted.Lazily)

    private val submissionState = MutableStateFlow<Resource<SubmissionUiState>>(resourceOf())

    val openAttachment = MutableSharedFlow<AttachmentItem>()

    val uiState = combine(
        submissionState,
        allowGradeSubmission
    ) { submissionState, allowGrade -> combine(submissionState, allowGrade) }
        .stateInResource(componentScope)

    init {
        componentScope.launch {
            submissionState.emitAll(
                combine(submission, attachments) { submissionRes, attachmentsRes ->
                    submissionRes.mapResource { submission ->
                        attachmentsRes.map { attachments ->
                            submission.toUiState(attachments.toAttachmentItems())
                        }
                    }
                }
            )
        }
    }

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

    fun onGrade(value: Int) {
        componentScope.launch {
            gradeSubmissionUseCase(submissionId, value)
                .onSuccess { response ->
                    submissionState.update { resource ->
                        resource.map { it.copy(grade = response.grade) }
                    }
                }
        }
    }

    fun onGradeCancel() {
        componentScope.launch {
            cancelGradeSubmissionUseCase(submissionId)
                .onSuccess { submissionState.update { resource -> resource.map { it.copy(grade = null) } } }
        }
    }
}