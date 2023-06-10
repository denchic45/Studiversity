package com.denchic45.studiversity.ui.coursework.yourSubmission

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.flatMapResourceFlow
import com.denchic45.studiversity.domain.map
import com.denchic45.studiversity.domain.mapResource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.CancelSubmissionUseCase
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindSubmissionAttachmentsUseCase
import com.denchic45.studiversity.domain.usecase.FindYourSubmissionUseCase
import com.denchic45.studiversity.domain.usecase.RemoveAttachmentFromSubmissionUseCase
import com.denchic45.studiversity.domain.usecase.SubmitSubmissionUseCase
import com.denchic45.studiversity.domain.usecase.UploadAttachmentToSubmissionUseCase
import com.denchic45.studiversity.ui.coursework.SubmissionUiState
import com.denchic45.studiversity.ui.coursework.toUiState
import com.denchic45.studiversity.ui.model.toAttachmentItems
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import com.denchic45.stuiversity.api.role.model.Capability
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import okio.Path
import java.util.UUID

@Inject
class YourSubmissionComponent(
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val findYourSubmissionUseCase: FindYourSubmissionUseCase,
    private val findSubmissionAttachmentsUseCase: FindSubmissionAttachmentsUseCase,
    private val uploadAttachmentToSubmissionUseCase: UploadAttachmentToSubmissionUseCase,
    private val removeAttachmentFromSubmissionUseCase: RemoveAttachmentFromSubmissionUseCase,
    private val submitSubmissionUseCase: SubmitSubmissionUseCase,
    private val cancelSubmissionUseCase: CancelSubmissionUseCase,
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

    private val _observeYourSubmission: Flow<Resource<SubmissionResponse>> =
        capabilities.flatMapResourceFlow {
            if (it.hasCapability(Capability.SubmitSubmission)) {
                flow { emit(findYourSubmissionUseCase(courseId, workId)) }
            } else {
                emptyFlow()
            }
        }.shareIn(componentScope, SharingStarted.Lazily)

    private val _updatedYourSubmission = MutableSharedFlow<Resource<SubmissionResponse>>()

    private val _attachments = _observeYourSubmission.flatMapResourceFlow {
        findSubmissionAttachmentsUseCase(courseId, workId, it.id)
    }.shareIn(componentScope, SharingStarted.Lazily)

    val uiState = MutableStateFlow<Resource<SubmissionUiState>>(Resource.Loading)

    val sheetExpanded = MutableStateFlow(false)

    private val backCallback = BackCallback { sheetExpanded.update { false }}

    init {
        backHandler.register(backCallback)
        componentScope.launch {
            sheetExpanded.collect {
                backCallback.isEnabled = it
            }
        }

        componentScope.launch {
            uiState.emitAll(
                combine(
                    merge(_observeYourSubmission, _updatedYourSubmission),
                    _attachments
                ) { submissionRes, attachmentsRes ->
                    submissionRes.mapResource { submission ->
                        attachmentsRes.map { attachments ->
                            submission.toUiState(attachments.toAttachmentItems())
                        }
                    }
                }
            )
        }
    }

    fun onFilesSelect(paths: List<Path>) {
        uiState.value.onSuccess { state ->
            componentScope.launch {
                paths.map { path ->
                    uploadAttachmentToSubmissionUseCase(
                        courseId = courseId,
                        workId = workId,
                        submissionId = state.id,
                        attachmentRequest = CreateFileRequest(path.toFile())
                    )
                }
            }
        }
    }

    fun onAttachmentRemove(attachmentId: UUID) {
        uiState.value.onSuccess {
            componentScope.launch {
                removeAttachmentFromSubmissionUseCase(attachmentId, courseId, workId, it.id)
            }
        }
    }

    fun onSubmit() {
        uiState.value.onSuccess {
            componentScope.launch {
                _updatedYourSubmission.emit(
                    submitSubmissionUseCase(courseId, workId, it.id)
                )
            }
        }
    }

    fun onCancel() {
        uiState.value.onSuccess {
            componentScope.launch {
                _updatedYourSubmission.emit(
                    cancelSubmissionUseCase(courseId, workId, it.id)
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