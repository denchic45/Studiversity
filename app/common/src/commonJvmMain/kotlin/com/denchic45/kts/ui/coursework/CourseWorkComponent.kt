package com.denchic45.kts.ui.coursework

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.kts.domain.usecase.DownloadFileUseCase
import com.denchic45.kts.domain.usecase.RemoveCourseElementUseCase
import com.denchic45.kts.ui.confirm.ConfirmDialogInteractor
import com.denchic45.kts.ui.confirm.ConfirmState
import com.denchic45.kts.ui.coursework.details.CourseWorkDetailsComponent
import com.denchic45.kts.ui.coursework.submissions.CourseWorkSubmissionsComponent
import com.denchic45.kts.ui.coursework.yourSubmission.YourSubmissionComponent
import com.denchic45.kts.ui.model.AttachmentItem
import com.denchic45.kts.ui.navigation.EmptyChildrenContainer
import com.denchic45.kts.ui.uiTextOf
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseWorkComponent(
    private val downloadFileUseCase: DownloadFileUseCase,
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
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
    private val _yourSubmissionComponent: (
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> YourSubmissionComponent,
    @Assisted
    private val onEditorOpen: (courseId: UUID, elementId: UUID?) -> Unit,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val elementId: UUID,
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
        elementId,
        componentContext.childContext("yourSubmission")
    )

    private val courseWorkDetailsComponent = _courseWorkDetailsComponent(
        courseId,
        elementId,
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
                add(
                    Child.Details(courseWorkDetailsComponent)
                )
                it.ifHasCapability(Capability.ReadSubmissions) {
                    add(
                        Child.Submissions(
                            _courseWorkSubmissionsComponent(
                                courseId,
                                elementId,
                                childContext("submissions")
                            )
                        )
                    )
                }
            }
        }
    }.stateInResource(componentScope)

    fun onEditClick() {
        onEditorOpen(courseId, elementId)
    }

    fun onRemoveClick() {
        confirmDialogInteractor.set(
            ConfirmState(
                title = uiTextOf("Удалить задание?"),
                text = uiTextOf("Оценки и ответы учащихся также будут удалены")
            )
        )

        componentScope.launch {
            if (confirmDialogInteractor.receiveConfirm()) {
                removeCourseElementUseCase(courseId, elementId).onSuccess {
                    withContext(Dispatchers.Main) {
                        onFinish()
                    }
                }
            }
        }
    }

    fun onAttachmentClick(item: AttachmentItem) {
        when (item) {
            is AttachmentItem.FileAttachmentItem -> when (item.state) {
                FileState.Downloaded -> componentScope.launch { openAttachment.emit(item) }
                FileState.Preview -> componentScope.launch {
                    downloadFileUseCase(item.attachmentId!!)
                }

                else -> {}
            }

            is AttachmentItem.LinkAttachmentItem -> componentScope.launch {
                openAttachment.emit(item)
            }
        }
    }

    sealed class Child(val title: String) {
        class Details(val component: CourseWorkDetailsComponent) : Child("Задание")
        class Submissions(val component: CourseWorkSubmissionsComponent) : Child("Ответы")
    }
}