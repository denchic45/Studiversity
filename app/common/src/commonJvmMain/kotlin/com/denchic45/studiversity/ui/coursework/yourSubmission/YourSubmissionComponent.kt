package com.denchic45.studiversity.ui.coursework.yourSubmission

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.denchic45.studiversity.domain.resource.*
import com.denchic45.studiversity.domain.usecase.*
import com.denchic45.studiversity.ui.attachments.AttachmentsComponent
import com.denchic45.studiversity.ui.coursework.SubmissionUiState
import com.denchic45.studiversity.ui.coursework.toUiState
import com.denchic45.studiversity.ui.model.toAttachmentItems
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.attachment.AttachmentResource
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import com.denchic45.stuiversity.api.role.model.Capability
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import okio.Path
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class YourSubmissionComponent(
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val findYourSubmissionUseCase: FindYourSubmissionUseCase,
    private val findSubmissionAttachmentsUseCase: FindSubmissionAttachmentsUseCase,
//    private val uploadAttachmentToSubmissionUseCase: UploadAttachmentToSubmissionUseCase,
//    private val removeAttachmentFromSubmissionUseCase: RemoveAttachmentFromSubmissionUseCase,
    private val submitSubmissionUseCase: SubmitSubmissionUseCase,
    private val cancelSubmissionUseCase: CancelSubmissionUseCase,
    _attachmentsComponent: (String, UUID, ComponentContext) -> AttachmentsComponent,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val workId: UUID,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    private val capabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = courseId,
        capabilities = listOf(Capability.ReadSubmissions, Capability.SubmitSubmission)
    ).stateInResource(componentScope)

    val hasSubmission = capabilities.mapResource {
        it.hasCapability(Capability.SubmitSubmission)
    }.stateInResource(componentScope)

    private val _observeYourSubmission: Flow<Resource<SubmissionResponse>> =
        hasSubmission.flatMapResourceFlow { has ->
            if (has) {
                flow { emit(findYourSubmissionUseCase(courseId, workId)) }
            } else {
                emptyFlow()
            }
        }.shareIn(componentScope, SharingStarted.Lazily)

    private val _updatedYourSubmission = MutableSharedFlow<Resource<SubmissionResponse>>()

    private val attachmentsComponentResource = _observeYourSubmission
        .filterNotNullValue()
        .mapResource {
            _attachmentsComponent(AttachmentResource.SUBMISSION, it.id, componentContext.childContext("Attachments"))
        }

    private val attachmentComponentFlow = attachmentsComponentResource.filterSuccess().mapToValue()

    private suspend fun attachmentComponent() = attachmentComponentFlow.first()

//    private val _attachments = _observeYourSubmission
//        .filterNotNullValue()
//        .flatMapResourceFlow {
//            findSubmissionAttachmentsUseCase(courseId, workId, it.id)
//        }.shareIn(componentScope, SharingStarted.Lazily)

    val submission = MutableStateFlow<Resource<SubmissionUiState>>(Resource.Loading)

    val sheetExpanded = MutableStateFlow(false)

    private val backCallback = BackCallback { sheetExpanded.update { false } }

    init {
        backHandler.register(backCallback)
        componentScope.launch {
            sheetExpanded.collect {
                backCallback.isEnabled = it
            }
        }

        componentScope.launch {
            val combine = combine(
                merge(_observeYourSubmission, _updatedYourSubmission),
                attachmentComponentFlow
            ) { submissionRes, attachmentsComponentFlow ->
                attachmentsComponentFlow.attachments.flatMapResource { attachments ->
                    submissionRes.map { submission ->
                        submission.toUiState(attachments.toAttachmentItems())
                    }
                }
            }.flattenMerge()
            submission.emitAll(combine)
        }
    }

    fun onFilesSelect(paths: List<Path>) {
        submission.value.onSuccess { submissionUiState ->
            componentScope.launch {
                paths.map { path ->
                    attachmentComponent().uploadAttachmentByResource(CreateFileRequest(path.toFile()))
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
        submission.value.onSuccess {
            componentScope.launch {
                attachmentComponent().removeAttachment(attachmentId) // TODO потом поменять способ удаления
//                removeAttachmentFromSubmissionUseCase(attachmentId, courseId, workId, it.id)
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

    fun onExpandChanged(expanded: Boolean) {
        sheetExpanded.update { expanded }
    }

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