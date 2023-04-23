package com.denchic45.kts.ui.courseWork.submissiondetails

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindSubmissionAttachmentsUseCase
import com.denchic45.kts.domain.usecase.FindSubmissionByIdUseCase
import com.denchic45.kts.ui.courseWork.toUiState
import com.denchic45.kts.ui.model.toAttachmentItems
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID


@Inject
class SubmissionDetailsComponent(
    private val findSubmissionByIdUseCase: FindSubmissionByIdUseCase,
    private val findSubmissionAttachmentsUseCase: FindSubmissionAttachmentsUseCase,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val elementId: UUID,
    @Assisted
    private val submissionId: UUID,
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    private val submission = flow {
        emit(findSubmissionByIdUseCase(courseId, elementId, submissionId))
    }.shareIn(componentScope, SharingStarted.Lazily)

    private val attachments = findSubmissionAttachmentsUseCase(courseId, elementId, submissionId)
        .shareIn(componentScope, SharingStarted.Lazily)

    val uiState = combine(submission, attachments) { submissionRes, attachmentsRes ->
        submissionRes.mapResource { submission ->
            attachmentsRes.map { attachments ->
                submission.toUiState(attachments.toAttachmentItems())
            }
        }
    }.stateInResource(componentScope)
}