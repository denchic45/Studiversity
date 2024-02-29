package com.denchic45.studiversity.ui.coursework.submissions

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.resourceOf
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.FindCourseWorkSubmissionsUseCase
import com.denchic45.studiversity.ui.coursework.submissiondetails.SubmissionDetailsComponent
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseWorkSubmissionsComponent(
    private val findCourseWorkSubmissionsUseCase: FindCourseWorkSubmissionsUseCase,
    private val _submissionDetailsComponent: (
        courseId: UUID,
        elementId: UUID,
        submissionId: UUID,
        ComponentContext
    ) -> SubmissionDetailsComponent,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val elementId: UUID,
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

 private   val observedSubmissions = flow {
        emit(findCourseWorkSubmissionsUseCase(courseId, elementId))
    }.stateInResource(componentScope)

    val submissions = MutableStateFlow<Resource<List<SubmissionResponse>>>(resourceOf())

    val refreshing = MutableStateFlow(false)

    init {
        componentScope.launch {
            submissions.emitAll(observedSubmissions)
        }
    }

    private val overlayNavigation = SlotNavigation<SubmissionConfig>()

    val childSlot = childSlot(
        handleBackButton = true,
        source = overlayNavigation
    ) { config, componentContext ->
        SubmissionChild(
            _submissionDetailsComponent(
                courseId,
                elementId,
                config.submissionId,
                componentContext
            )
        )
    }

    fun onRefresh() {
        componentScope.launch {
            refreshing.update { true }
            submissions.update { findCourseWorkSubmissionsUseCase(courseId, elementId) }
            refreshing.update { false }
        }
    }

    fun onSubmissionClick(submissionId: UUID) {
        overlayNavigation.activate(SubmissionConfig(submissionId))
    }

    fun onSubmissionClose() {
        overlayNavigation.dismiss()
    }

    @Parcelize
    class SubmissionConfig(val submissionId: UUID) : Parcelable

    class SubmissionChild(val component: SubmissionDetailsComponent)

}
