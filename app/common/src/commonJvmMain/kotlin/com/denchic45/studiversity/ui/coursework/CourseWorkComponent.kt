package com.denchic45.studiversity.ui.coursework

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.studiversity.domain.model.Attachment2
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.DownloadFileUseCase
import com.denchic45.studiversity.domain.usecase.FindCourseWorkAttachmentsUseCase
import com.denchic45.studiversity.domain.usecase.RemoveCourseElementUseCase
import com.denchic45.studiversity.ui.attachments.AttachmentsComponent
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.coursework.details.CourseWorkDetailsComponent
import com.denchic45.studiversity.ui.coursework.submissions.CourseWorkSubmissionsComponent
import com.denchic45.studiversity.ui.coursework.yourSubmission.YourSubmissionComponent
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.ui.navigation.EmptyChildrenContainer
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseWorkComponent(
    private val findCourseWorkAttachmentsUseCase: FindCourseWorkAttachmentsUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val removeCourseElementUseCase: RemoveCourseElementUseCase,
    _courseWorkDetailsComponent: (
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> CourseWorkDetailsComponent,
    private val _courseWorkSubmissionsComponent: (
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> CourseWorkSubmissionsComponent,
    _yourSubmissionComponent: (
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> YourSubmissionComponent,
    attachmentsComponent: (attachments: Flow<Resource<List<Attachment2>>>) -> AttachmentsComponent,
    @Assisted
    private val onEditorOpen: (courseId: UUID, elementId: UUID?) -> Unit,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val workId: UUID,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext, EmptyChildrenContainer {

    private val componentScope = componentScope()

    val openAttachment = MutableSharedFlow<AttachmentItem>()

    private val capabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = courseId,
        capabilities = listOf(
            Capability.ReadSubmissions,
            Capability.WriteCourseElements,
            Capability.SubmitSubmission
        )
    ).stateInResource(componentScope)

    val yourSubmissionComponent = _yourSubmissionComponent(
        courseId,
        workId,
        componentContext.childContext("yourSubmission")
    )

    val attachmentsComponent = attachmentsComponent(findCourseWorkAttachmentsUseCase(workId))

    private val courseWorkDetailsComponent = _courseWorkDetailsComponent(
        courseId,
        workId,
        childContext("details")
    )

    val allowEditWork = capabilities.map {
        when (it) {
            is Resource.Success -> it.value.hasCapability(Capability.WriteCourseElements)
            is Resource.Error,
            Resource.Loading,
            -> false
        }
    }

    val children = capabilities.mapResource {
        buildList {
            withContext(Dispatchers.Main) {
                add(Child.Details(courseWorkDetailsComponent))
                it.ifHasCapability(Capability.ReadSubmissions) {
                    add(
                        Child.Submissions(
                            _courseWorkSubmissionsComponent(
                                courseId,
                                workId,
                                childContext("submissions")
                            )
                        )
                    )
                }
            }
        }
    }.stateInResource(componentScope)

    fun onEditClick() {
        onEditorOpen(courseId, workId)
    }

    fun onDeleteClick() {
        confirmDialogInteractor.set(
            ConfirmState(
                title = uiTextOf("Удалить задание?"),
                text = uiTextOf("Оценки и ответы учащихся также будут удалены")
            )
        )

        componentScope.launch {
            if (confirmDialogInteractor.receiveConfirm()) {
                removeCourseElementUseCase(courseId, workId).onSuccess {
                    withContext(Dispatchers.Main) {
                        onFinish()
                    }
                }
            }
        }
    }

//    fun onAttachmentClick(item: AttachmentItem) {
//        when (item) {
//            is AttachmentItem.FileAttachmentItem -> when (item.state) {
//                FileState.Downloaded -> componentScope.launch { openAttachment.emit(item) }
//                FileState.Preview, FileState.FailDownload -> componentScope.launch {
//                    downloadFileUseCase(item.attachmentId)
//                }
//
//                else -> {}
//            }
//
//            is AttachmentItem.LinkAttachmentItem -> componentScope.launch {
//                openAttachment.emit(item)
//            }
//        }
//    }

    sealed class Child(val title: String) {
        class Details(val component: CourseWorkDetailsComponent) : Child("Задание")
        class Submissions(val component: CourseWorkSubmissionsComponent) : Child("Ответы")
    }
}