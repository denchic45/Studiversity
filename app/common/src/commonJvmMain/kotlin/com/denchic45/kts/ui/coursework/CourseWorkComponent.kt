package com.denchic45.kts.ui.coursework

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.kts.domain.usecase.RemoveCourseElementUseCase
import com.denchic45.kts.ui.coursework.details.CourseWorkDetailsComponent
import com.denchic45.kts.ui.coursework.submissions.CourseWorkSubmissionsComponent
import com.denchic45.kts.ui.coursework.yourSubmission.YourSubmissionComponent
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseWorkComponent(
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val removeCourseElementUseCase: RemoveCourseElementUseCase,
    private val _courseWorkDetailsComponent: (
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
    private val courseId: UUID,
    @Assisted
    private val elementId: UUID,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val capabilities = flow {
        emit(
            checkUserCapabilitiesInScopeUseCase(
                scopeId = courseId,
                capabilities = listOf(Capability.ReadSubmissions, Capability.SubmitSubmission)
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