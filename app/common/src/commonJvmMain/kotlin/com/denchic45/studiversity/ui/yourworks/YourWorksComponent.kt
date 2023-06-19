package com.denchic45.studiversity.ui.yourworks

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.ui.coursework.CourseWorkComponent
import com.denchic45.studiversity.ui.courseworkeditor.CourseWorkEditorComponent
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class YourWorksComponent(
    _yourUpcomingWorksComponent: (onWorkOpen: (UUID, UUID) -> Unit, ComponentContext) -> YourUpcomingWorksComponent,
    _yourOverdueWorksComponent: (onWorkOpen: (UUID, UUID) -> Unit, ComponentContext) -> YourOverdueWorksComponent,
    _yourSubmittedWorksComponent: (onWorkOpen: (UUID, UUID) -> Unit, ComponentContext) -> YourSubmittedWorksComponent,
    private val _courseWorkComponent: (
        onEdit: (courseId: UUID, elementId: UUID?) -> Unit,
        onFinish: () -> Unit,
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> CourseWorkComponent,
    courseWorkEditorComponent: (
        onFinish: () -> Unit,
        courseId: UUID,
        workId: UUID?,
        topicId: UUID?,
        ComponentContext,
    ) -> CourseWorkEditorComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val overlayNavigation = OverlayNavigation<OverlayConfig>()

    val childOverlay = childOverlay(
        source = overlayNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is OverlayConfig.CourseWork -> OverlayChild.CourseWork(
                    createCourseWorkComponent(
                        config.courseId,
                        config.workId,
                        context
                    )
                )

                is OverlayConfig.CourseWorkEditor -> OverlayChild.CourseWorkEditor(
                    courseWorkEditorComponent(
                        { overlayNavigation.dismiss() },
                        config.courseId,
                        config.workId,
                        null,
                        context
                    )
                )
            }
        }
    )

    private fun createCourseWorkComponent(courseId: UUID, workId: UUID, context: ComponentContext) =
        _courseWorkComponent(
            { courseId, workId ->
                overlayNavigation.activate(OverlayConfig.CourseWorkEditor(courseId, workId))
            },
            overlayNavigation::dismiss,
            courseId,
            workId,
            context
        )

    val tabChildren = listOf(
        TabChild.Upcoming(
            _yourUpcomingWorksComponent(
                ::onWorkOpen,
                componentContext.childContext("Upcoming")
            )
        ),
        TabChild.Overdue(
            _yourOverdueWorksComponent(
                ::onWorkOpen,
                componentContext.childContext("Overdue")
            )
        ),
        TabChild.Submitted(
            _yourSubmittedWorksComponent(
                ::onWorkOpen,
                componentContext.childContext("Submitted")
            )
        )
    )

    private fun onWorkOpen(courseId: UUID, workId: UUID) {
        overlayNavigation.activate(
            OverlayConfig.CourseWork(
                courseId,
                workId
            )
        )
    }

    sealed class TabChild(val title: String) {
        class Upcoming(val component: YourUpcomingWorksComponent) : TabChild("Предстоящие")
        class Overdue(val component: YourOverdueWorksComponent) : TabChild("Просроченные")
        class Submitted(val component: YourSubmittedWorksComponent) : TabChild("Сданные")
    }

    @Parcelize
    sealed interface OverlayConfig : Parcelable {
        data class CourseWork(val courseId: UUID, val workId: UUID) : OverlayConfig
        data class CourseWorkEditor(val courseId: UUID, val workId: UUID?) : OverlayConfig
    }

    sealed interface OverlayChild {
        class CourseWork(val component: CourseWorkComponent) : OverlayChild
        class CourseWorkEditor(val component: CourseWorkEditorComponent) : OverlayChild
    }
}