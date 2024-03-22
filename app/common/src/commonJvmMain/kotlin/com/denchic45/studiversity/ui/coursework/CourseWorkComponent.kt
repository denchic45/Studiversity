package com.denchic45.studiversity.ui.coursework

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.RemoveCourseElementUseCase
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.coursework.details.CourseWorkDetailsComponent
import com.denchic45.studiversity.ui.coursework.submissions.CourseWorkSubmissionsComponent
import com.denchic45.studiversity.ui.coursework.yourSubmission.YourSubmissionComponent
import com.denchic45.studiversity.ui.navigation.EmptyChildrenContainer
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseWorkComponent(
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val removeCourseElementUseCase: RemoveCourseElementUseCase,
    courseWorkDetailsComponent: (
        elementId: UUID,
        ComponentContext,
    ) -> CourseWorkDetailsComponent,
    private val courseWorkSubmissionsComponent: (
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> CourseWorkSubmissionsComponent,
    yourSubmissionComponent: (
        courseId: UUID,
        workId: UUID,
        ComponentContext,
    ) -> YourSubmissionComponent,

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

    private val capabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = courseId,
        capabilities = listOf(
            Capability.ReadSubmissions,
            Capability.WriteCourseElements,
            Capability.SubmitSubmission
        )
    ).stateInResource(componentScope)

    val yourSubmissionComponent = capabilities.mapResource {
        if (it.hasCapability(Capability.SubmitSubmission))
            yourSubmissionComponent(
                courseId,
                workId,
                componentContext.childContext("YourSubmission")
            )
        else null
    }.stateInResource(componentScope)

    private val courseWorkDetailsComponent = courseWorkDetailsComponent(
        workId,
        childContext("Details")
    )

    val allowEditWork = capabilities.mapResource {
        it.hasCapability(Capability.WriteCourseElements)
    }.stateInResource(componentScope)

    val children = capabilities.mapResource {
        buildList {
            withContext(Dispatchers.Main) {
                add(Child.Details(this@CourseWorkComponent.courseWorkDetailsComponent))
                it.ifHasCapability(Capability.ReadSubmissions) {
                    add(
                        Child.Submissions(
                            courseWorkSubmissionsComponent(
                                courseId,
                                workId,
                                childContext("Submissions")
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

    sealed class Child(val title: String) {
        class Details(val component: CourseWorkDetailsComponent) : Child("Задание")
        class Submissions(val component: CourseWorkSubmissionsComponent) : Child("Ответы")
    }
}