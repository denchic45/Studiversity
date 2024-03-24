package com.denchic45.studiversity.ui.coursework.yourSubmission

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.studiversity.domain.model.Attachment2
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.filterNotNullValue
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.onFailure
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.AddAttachmentToSubmissionUseCase
import com.denchic45.studiversity.domain.usecase.CancelSubmissionUseCase
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindSubmissionAttachmentsUseCase
import com.denchic45.studiversity.domain.usecase.ObserveYourSubmissionUseCase
import com.denchic45.studiversity.domain.usecase.RemoveAttachmentFromSubmissionUseCase
import com.denchic45.studiversity.domain.usecase.SubmitSubmissionUseCase
import com.denchic45.studiversity.ui.attachments.AttachmentsComponent
import com.denchic45.studiversity.ui.coursework.SubmissionUiState
import com.denchic45.studiversity.ui.coursework.toUiState
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.submission.model.SubmissionResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import okio.Path
import java.util.UUID


@Inject
class YourSubmissionComponent(
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val addAttachmentToSubmissionUseCase: AddAttachmentToSubmissionUseCase,
    private val removeAttachmentFromSubmissionUseCase: RemoveAttachmentFromSubmissionUseCase,
    observeYourSubmissionUseCase: ObserveYourSubmissionUseCase,
    private val findSubmissionAttachmentsUseCase: FindSubmissionAttachmentsUseCase,
    private val submitSubmissionUseCase: SubmitSubmissionUseCase,
    private val cancelSubmissionUseCase: CancelSubmissionUseCase,
    attachmentsComponent: (
        attachments: Flow<Resource<List<Attachment2>>>,
        onAddAttachment: ((AttachmentRequest) -> Unit)?,
        onRemoveAttachment: ((UUID) -> Unit)?,
        ComponentContext
    ) -> AttachmentsComponent,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val workId: UUID,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    private val _observeYourSubmission = observeYourSubmissionUseCase(courseId, workId)
        .shareIn(componentScope, SharingStarted.Lazily)

    private val _updatedYourSubmission = MutableSharedFlow<Resource<SubmissionResponse>>()

    val attachmentsComponentResource = _observeYourSubmission
        .filterNotNullValue()
        .mapResource { submission ->
            attachmentsComponent(
                findSubmissionAttachmentsUseCase(submission.id),
                { componentScope.launch { addAttachmentToSubmissionUseCase(submission.id, it) } },
                {
                    componentScope.launch {
                        removeAttachmentFromSubmissionUseCase(
                            submission.id,
                            it
                        )
                    }
                },
                componentContext.childContext("Attachments")
            )
        }.stateInResource(componentScope)

    val submission = MutableStateFlow<Resource<SubmissionUiState>>(Resource.Loading)

//    val sheetExpanded = MutableStateFlow(false)

//    private val backCallback = BackCallback { sheetExpanded.update { false } }

    init {
//        backHandler.register(backCallback)
//        componentScope.launch {
//            sheetExpanded.collect {
//                backCallback.isEnabled = it
//            }
//        }

        componentScope.launch {
            submission.emitAll(
                merge(_observeYourSubmission, _updatedYourSubmission)
                    .mapResource(SubmissionResponse::toUiState)
            )
        }
    }

    fun onFilesSelect(paths: List<Path>) {
        submission.value.onSuccess { submissionUiState ->
            componentScope.launch {
                paths.map { path ->
                    addAttachmentToSubmissionUseCase(
                        submissionUiState.id,
                        CreateFileRequest(path.toFile())
                    )
                        .onSuccess {
                            println("success load: $it")
                        }.onFailure {
                            println("failed load: $it")
                        }
                }
            }
        }
    }

    fun onAttachmentRemove(attachmentId: UUID) {
        submission.value.onSuccess { submission ->
            componentScope.launch {
                removeAttachmentFromSubmissionUseCase(submission.id, attachmentId)
            }
        }
    }

    fun onSubmit() {
        submission.value.onSuccess {
            componentScope.launch {
                _updatedYourSubmission.emit(
                    submitSubmissionUseCase(it.id)
                )
            }
        }
    }

    fun onCancel() {
        submission.value.onSuccess {
            componentScope.launch {
                _updatedYourSubmission.emit(
                    cancelSubmissionUseCase(it.id)
                )
            }
        }
    }

//    fun onExpandChanged(expanded: Boolean) {
//        sheetExpanded.update { expanded }
//    }

//    fun List<AttachmentItem>.toAttachmentRequests(): List<AttachmentRequest> {
//        return map { attachment ->
//            when (attachment) {
//                is AttachmentItem.FileAttachmentItem -> {
//                    CreateFileRequest(
//                        name = attachment.name,
//                        bytes = attachment.path.toFile().readBytes()
//                    )
//                }
//
//                is AttachmentItem.LinkAttachmentItem -> CreateLinkRequest(
//                    url = attachment.url
//                )
//            }
//        }
//    }
}