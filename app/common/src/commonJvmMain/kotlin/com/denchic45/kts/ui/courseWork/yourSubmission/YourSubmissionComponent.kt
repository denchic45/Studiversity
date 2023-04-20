package com.denchic45.kts.ui.courseWork.yourSubmission

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.domain.model.Attachment2
import com.denchic45.kts.data.domain.model.FileAttachment2
import com.denchic45.kts.data.domain.model.LinkAttachment2
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.flatMapResourceFlow
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindSubmissionAttachmentsUseCase
import com.denchic45.kts.domain.usecase.FindYourSubmissionUseCase
import com.denchic45.kts.ui.model.AttachmentItem
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import com.denchic45.stuiversity.api.course.work.grade.GradeResponse
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import com.denchic45.stuiversity.api.course.work.submission.model.WorkSubmissionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
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
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val workId: UUID,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {


    private val componentScope = componentScope()

    private val addedAttachments = mutableListOf<AttachmentRequest>()

    private val yourSubmission = flow { emit(findYourSubmissionUseCase(courseId, workId)) }
        .stateInResource(componentScope)

    private val _attachments = yourSubmission.flatMapResourceFlow {
        findSubmissionAttachmentsUseCase(courseId, workId, it.id)
    }

    val uiState = MutableStateFlow<Resource<SubmissionUiState>>(Resource.Loading)

    init {
        componentScope.launch {
            combine(yourSubmission, _attachments) { submission,attachments->

            }
            .collect {
                it.map {
                    (it as WorkSubmissionResponse).let {
                        SubmissionUiState(
                            attachments = it.content.attachments.toatt
                        )
                    }
                }
            }
        }
    }

    fun onAttachmentSelect(path: Path) {

    }

    fun List<Attachment2>.toAttachmentItems(): List<AttachmentItem> {
        return map { attachment ->
            when (attachment) {
                is FileAttachment2 -> AttachmentItem.FileAttachmentItem(
                    name = attachment.name,
                    previewUrl = null,
                    attachmentId = attachment.id,
                    state = attachment.state,
                    file = attachment.path
                )

                is LinkAttachment2 -> AttachmentItem.LinkAttachmentItem(
                    name = attachment.url,
                    previewUrl = null,
                    attachmentId = attachment.id,
                    url = attachment.url
                )
            }
        }
    }

    data class SubmissionUiState(
        val attachments: List<AttachmentItem>,
        val grade: GradeResponse?,
        val state: SubmissionState,
        val updatedAt: LocalDateTime?
    )

}