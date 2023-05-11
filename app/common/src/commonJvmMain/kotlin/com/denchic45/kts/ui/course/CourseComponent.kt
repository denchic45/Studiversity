package com.denchic45.kts.ui.course

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.kts.domain.usecase.FindCourseByIdUseCase
import com.denchic45.kts.ui.CourseMembersComponent
import com.denchic45.kts.ui.courseelements.CourseElementsComponent
import com.denchic45.kts.ui.coursetimetable.CourseTimetableComponent
import com.denchic45.kts.ui.coursetopics.CourseTopicsComponent
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID


@Inject
class CourseComponent(
    findCourseByIdUseCase: FindCourseByIdUseCase,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    _courseTopicsComponent: (
        courseId: UUID,
        ComponentContext
    ) -> CourseTopicsComponent,
    _courseElementsComponent: (
        courseId: UUID,
        onElementOpen: (courseId: UUID, elementId: UUID) -> Unit,
        ComponentContext
    ) -> CourseElementsComponent,
    _courseMembersComponent: (
        courseId: UUID,
        onMemberOpen: (memberId: UUID) -> Unit,
        ComponentContext
    ) -> CourseMembersComponent,
    _courseTimetableComponent: (
        courseId: UUID,
        ComponentContext
    ) -> CourseTimetableComponent,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val onCourseEditorOpen: (courseId: UUID) -> Unit,
    @Assisted
    private val onElementOpen: (courseId: UUID, elementId: UUID) -> Unit,
    @Assisted
    private val onCourseElementEditorOpen: (courseId: UUID, elementId: UUID?) -> Unit,
    @Assisted
    private val onCourseTopicsOpen: (courseId: UUID) -> Unit,
    @Assisted
    private val onMemberOpen: (memberId: UUID) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val course = flow { emit(findCourseByIdUseCase(courseId)) }.stateInResource(componentScope)

    val allowEdit = flow {
        emit(
            when (val resource = checkUserCapabilitiesInScopeUseCase(
                scopeId = courseId,
                capabilities = listOf(Capability.WriteCourse)
            )) {
                Resource.Loading,
                is Resource.Error -> false

                is Resource.Success -> {
                    resource.value.hasCapability(Capability.WriteCourse)
                }
            }
        )
    }

    private val courseElementsComponent = _courseElementsComponent(
        courseId,
        onElementOpen,
        componentContext.childContext("elements")
    )

    private val courseMembersComponent = _courseMembersComponent(
        courseId,
        onMemberOpen,
        componentContext.childContext("members")
    )

    private val courseTimetableComponent = _courseTimetableComponent(
        courseId,
        componentContext.childContext("timetable")
    )

    val capabilities = flow {
        emit(
            checkUserCapabilitiesInScopeUseCase(
                scopeId = courseId,
                capabilities = listOf()
            )
        )
    }.shareIn(componentScope, SharingStarted.Lazily)

    private val defaultChildren =
        listOf(
            Child.Elements(courseElementsComponent),
            Child.Members(courseMembersComponent),
            Child.Timetable(courseTimetableComponent)
        )

    val children = capabilities.mapResource {
        defaultChildren
    }.stateInResource(
        scope = componentScope,
        initialValue = Resource.Success(defaultChildren)
    )

    private val overlayNavigation = OverlayNavigation<OverlayConfig>()
    val childOverlay = childOverlay<OverlayConfig, OverlayChild>(
        source = overlayNavigation,
        handleBackButton = true,
        childFactory = { config, componentContext ->
            when (config) {
                is OverlayConfig.Topics -> OverlayChild.Topics(
                    _courseTopicsComponent(courseId, componentContext)
                )
            }
        }
    )

    fun onFabClick() {
        onCourseElementEditorOpen(courseId, null)
    }

    fun onCourseEditClick() {
        onCourseEditorOpen(courseId)
    }

    fun onTopicEditClick() {
        onCourseTopicsOpen(courseId)
    }

    sealed class Child(val title: String) {

        class Elements(val component: CourseElementsComponent) : Child("Элементы")

        class Members(val component: CourseMembersComponent) : Child("Участники")

        class Timetable(val component: CourseTimetableComponent) : Child("Расписание")
    }

    @Parcelize
    sealed class OverlayConfig : Parcelable {
        data class Topics(val courseId: UUID) : OverlayConfig()
    }

    sealed class OverlayChild {
        class Topics(val component: CourseTopicsComponent) : OverlayChild()
    }
}