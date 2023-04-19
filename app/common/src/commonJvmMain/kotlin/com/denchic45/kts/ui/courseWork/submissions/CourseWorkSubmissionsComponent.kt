package com.denchic45.kts.ui.courseWork.submissions

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindCourseWorkSubmissionsUseCase
import com.denchic45.kts.ui.courseWork.submissiondetails.SubmissionDetailsComponent
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseWorkSubmissionsComponent(
    private val findCourseWorkSubmissionsUseCase: FindCourseWorkSubmissionsUseCase,
    private val _submissionDetailsComponent: (
        courseId: UUID,
        elementId: UUID,
        authorId: UUID,
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

    val submissions = flow {
        emit(findCourseWorkSubmissionsUseCase(courseId, elementId))
    }.stateInResource(componentScope)

    private val overlayNavigation = OverlayNavigation<SubmissionConfig>()

    val childOverlay = childOverlay(source = overlayNavigation) { config, componentContext ->
        SubmissionChild(
            _submissionDetailsComponent(
                courseId,
                elementId,
                config.authorId,
                componentContext
            )
        )
    }

    fun onSubmissionClick(authorId: UUID) {
        overlayNavigation.activate(SubmissionConfig(authorId))
    }

    @Parcelize
    class SubmissionConfig(val authorId: UUID) : Parcelable

    class SubmissionChild(val component: SubmissionDetailsComponent)

}
