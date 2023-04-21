package com.denchic45.kts.ui.courseWork.yourSubmission

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.domain.model.Attachment2
import com.denchic45.kts.data.domain.model.FileAttachment2
import com.denchic45.kts.data.domain.model.LinkAttachment2
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.flatMapResourceFlow
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.usecase.CancelSubmissionUseCase
import com.denchic45.kts.domain.usecase.FindSubmissionAttachmentsUseCase
import com.denchic45.kts.domain.usecase.FindYourSubmissionUseCase
import com.denchic45.kts.domain.usecase.SubmitSubmissionUseCase
import com.denchic45.kts.domain.usecase.UploadAttachmentToSubmissionUseCase
import com.denchic45.kts.ui.model.AttachmentItem
import com.denchic45.kts.ui.model.toAttachmentItems
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.course.element.model.CreateLinkRequest
import com.denchic45.stuiversity.api.course.work.grade.GradeResponse
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import okio.Path
import java.time.LocalDateTime
import java.util.UUID

@Inject
class YourSubmissionComponent(
    private val findYourSubmissionUseCase: FindYourSubmissionUseCase,
    private val findSubmissionAttachmentsUseCase: FindSubmissionAttachmentsUseCase,
    private val uploadAttachmentToSubmissionUseCase: UploadAttachmentToSubmissionUseCase,
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

    private val _observeYourSubmission = flow { emit(findYourSubmissionUseCase(courseId, workId)) }
        .shareIn(componentScope, SharingStarted.Lazily)

    private val _updatedYourSubmission = MutableSharedFlow<Resource<SubmissionResponse>>()

    private val _attachments = _observeYourSubmission.flatMapResourceFlow {
        findSubmissionAttachmentsUseCase(courseId, workId, it.id)
    }

    val uiState = MutableStateFlow<Resource<SubmissionUiState>>(Resource.Loading)

    init {
        componentScope.launch {
            uiState.emitAll(
                combine(
                    merge(_observeYourSubmission, _updatedYourSubmission),
                    _attachments
                ) { submissionRes, attachmentsRes ->
                    submissionRes.mapResource { submission ->
                        attachmentsRes.map { attachments ->
                            SubmissionUiState(
                                id = submission.id,
                                attachments = attachments.toAttachmentItems(),
                                grade = submission.grade,
                                state = submission.state,
                                updatedAt = submission.updatedAt
                            )
                        }
                    }
                }
            )
        }
    }

    fun onAttachmentSelect(path: Path) {
        uiState.value.onSuccess {
            componentScope.launch {
                uploadAttachmentToSubmissionUseCase(
                    courseId = courseId,
                    workId = workId,
                    submissionId = it.id,
                    attachmentRequest = CreateFileRequest(path.toFile())
                )
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

    fun List<AttachmentItem>.toAttachmentRequests(): List<AttachmentRequest> {
        return map { attachment ->
            when (attachment) {
                is AttachmentItem.FileAttachmentItem -> {
                    CreateFileRequest(
                        name = attachment.name,
                        bytes = attachment.path.toFile().readBytes()
                    )
                }

                is AttachmentItem.LinkAttachmentItem -> CreateLinkRequest(
                    url = attachment.url
                )
            }
        }
    }

    data class SubmissionUiState(
        val id: UUID,
        val attachments: List<AttachmentItem>,
        val grade: GradeResponse?,
        val state: SubmissionState,
        val updatedAt: LocalDateTime?
    )
}