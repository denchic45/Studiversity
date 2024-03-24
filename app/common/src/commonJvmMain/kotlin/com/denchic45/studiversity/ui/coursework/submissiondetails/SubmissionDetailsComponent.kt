package com.denchic45.studiversity.ui.coursework.submissiondetails

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.studiversity.domain.model.Attachment2
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.combine
import com.denchic45.studiversity.domain.resource.map
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.resourceOf
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.CancelGradeSubmissionUseCase
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindSubmissionAttachmentsUseCase
import com.denchic45.studiversity.domain.usecase.FindSubmissionByIdUseCase
import com.denchic45.studiversity.domain.usecase.GradeSubmissionUseCase
import com.denchic45.studiversity.ui.attachments.AttachmentsComponent
import com.denchic45.studiversity.ui.coursework.SubmissionUiState
import com.denchic45.studiversity.ui.coursework.toUiState
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.submission.model.SubmissionResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID


@Inject
class SubmissionDetailsComponent(
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val findSubmissionByIdUseCase: FindSubmissionByIdUseCase,
    findSubmissionAttachmentsUseCase: FindSubmissionAttachmentsUseCase,
    private val gradeSubmissionUseCase: GradeSubmissionUseCase,
    private val cancelGradeSubmissionUseCase: CancelGradeSubmissionUseCase,
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
    private val submissionId: UUID,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val attachmentsComponent = attachmentsComponent(
        findSubmissionAttachmentsUseCase(workId), null, null, childContext("Attachments")
    )

    private val capabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = courseId, capabilities = listOf(Capability.GradeSubmission)
    ).shareIn(componentScope, SharingStarted.Lazily)

    private val allowGradeSubmission = capabilities.mapResource {
        it.hasCapability(Capability.GradeSubmission)
    }.stateInResource(componentScope)

    private val submission = flow {
        emit(findSubmissionByIdUseCase(submissionId))
    }.shareIn(componentScope, SharingStarted.Lazily, 1)


    private val submissionState = MutableStateFlow<Resource<SubmissionUiState>>(resourceOf())

    val uiState = combine(
        submissionState,
        allowGradeSubmission
    ) { submissionState, allowGrade -> combine(submissionState, allowGrade) }
        .stateInResource(componentScope)

    init {
        componentScope.launch {
            submissionState.emitAll(submission.mapResource(SubmissionResponse::toUiState))
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