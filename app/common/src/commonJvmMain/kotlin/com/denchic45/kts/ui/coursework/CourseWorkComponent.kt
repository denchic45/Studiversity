package com.denchic45.kts.ui.coursework

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.kts.domain.usecase.RemoveCourseElementUseCase
import com.denchic45.kts.ui.confirm.ConfirmDialogInteractor
import com.denchic45.kts.ui.confirm.ConfirmState
import com.denchic45.kts.ui.coursework.details.CourseWorkDetailsComponent
import com.denchic45.kts.ui.coursework.submissions.CourseWorkSubmissionsComponent
import com.denchic45.kts.ui.coursework.yourSubmission.YourSubmissionComponent
import com.denchic45.kts.ui.uiTextOf
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseWorkComponent(
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
    private val onEdit: (courseId: UUID, elementId: UUID?) -> Unit,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val elementId: UUID,
    @Assisted
    private val onFinish:()->Unit,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val capabilities = flow {
        emit(
            checkUserCapabilitiesInScopeUseCase(
                scopeId = courseId,
                capabilities = listOf(
                    Capability.ReadSubmissions,
                    Capability.WriteCourseElements,
                    Capability.SubmitSubmission
                )
            )
        )
    }.stateInResource(componentScope)

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
            Resource.Loading -> false
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
        onEdit(courseId, elementId)
    }

    fun onRemoveClick() {
        confirmDialogInteractor.set(
            ConfirmState(
                title = uiTextOf("Удалить задание?"),
                text = uiTextOf("Оценки и ответы учащихся также будут удалены")
            )
        )

        componentScope.launch {
            if(confirmDialogInteractor.receiveConfirm()) {
                removeCourseElementUseCase(courseId, elementId).onSuccess {
                    onFinish()
                }
            }
        }
    }


    init {
//        componentScope.launch {
//            capabilities.filterSuccess().collect {
//                if (it.value.hasCapability(Capability.SubmitSubmission)) {
//                    overlayNavigation.activate(YourSubmissionConfig)
//                } else {
//                    overlayNavigation.dismiss()
//                }
//            }
//        }
    }

    sealed class Child(val title: String) {
        class Details(val component: CourseWorkDetailsComponent) : Child("Задание")
        class Submissions(val component: CourseWorkSubmissionsComponent) : Child("Ответы")
    }

    @Parcelize
    object YourSubmissionConfig : Parcelable

    class YourSubmissionChild(val component: YourSubmissionComponent)
}