package com.denchic45.kts.ui.course

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.kts.domain.usecase.FindCourseByIdUseCase
import com.denchic45.kts.ui.CourseMembersComponent
import com.denchic45.kts.ui.courseeditor.CourseEditorComponent
import com.denchic45.kts.ui.courseelements.CourseElementsComponent
import com.denchic45.kts.ui.coursetimetable.CourseTimetableComponent
import com.denchic45.kts.ui.coursetopics.CourseTopicsComponent
import com.denchic45.kts.ui.coursework.CourseWorkComponent
import com.denchic45.kts.ui.courseworkeditor.CourseWorkEditorComponent
import com.denchic45.kts.ui.profile.ProfileComponent
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID


@Inject
class CourseComponent(
    findCourseByIdUseCase: FindCourseByIdUseCase,
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    _courseTopicsComponent: (
        courseId: UUID,
        ComponentContext,
    ) -> CourseTopicsComponent,
    _courseEditorComponent: (
        onFinish: () -> Unit,
        courseId: UUID?,
        ComponentContext,
    ) -> CourseEditorComponent,
    _courseWorkComponent: (
        onEdit: (courseId: UUID, elementId: UUID?) -> Unit,
        onFinish: () -> Unit,
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> CourseWorkComponent,
    _courseWorkEditorComponent: (
        onFinish: () -> Unit,
        courseId: UUID,
        workId: UUID?,
        topicId: UUID?,
        ComponentContext,
    ) -> CourseWorkEditorComponent,
    _courseElementsComponent: (
        courseId: UUID,
        onElementOpen: (courseId: UUID, elementId: UUID) -> Unit,
        ComponentContext,
    ) -> CourseElementsComponent,
    _courseMembersComponent: (
        courseId: UUID,
        onMemberOpen: (memberId: UUID) -> Unit,
        ComponentContext,
    ) -> CourseMembersComponent,
    _courseTimetableComponent: (
        courseId: UUID,
        ComponentContext,
    ) -> CourseTimetableComponent,
    _profileComponent: (
        onStudyGroupOpen: (UUID) -> Unit,
        UUID,
        ComponentContext,
    ) -> ProfileComponent,
    @Assisted
    private val onStudyGroupOpen: (studyGroupId: UUID) -> Unit,
    @Assisted
    private val courseId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val course = flow { emit(findCourseByIdUseCase(courseId)) }.stateInResource(componentScope)

    private val capabilitiesFlow = checkUserCapabilitiesInScopeUseCase(
        scopeId = courseId,
        capabilities = listOf(Capability.WriteCourse)
    ).stateInResource(componentScope)

    val allowEdit = capabilitiesFlow.map {
        when (val resource = it) {
            Resource.Loading,
            is Resource.Error,
            -> false

            is Resource.Success -> {
                resource.value.hasCapability(Capability.WriteCourse)
            }
        }
    }

    private val courseElementsComponent = _courseElementsComponent(
        courseId,
        { _, workId ->
            stackNavigation.push(
                Config.CourseWork(workId)
            )
        },
        componentContext.childContext("Elements")
    )

    private val courseMembersComponent = _courseMembersComponent(
        courseId,
        { sidebarNavigation.activate(SidebarConfig.Profile(it)) },
        componentContext.childContext("Members")
    )

    private val courseTimetableComponent = _courseTimetableComponent(
        courseId,
        componentContext.childContext("Timetable")
    )

    val capabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = courseId,
        capabilities = listOf()
    ).shareIn(componentScope, SharingStarted.Lazily)

    private val defaultTabChildren =
        listOf(
            TabChild.Elements(courseElementsComponent),
            TabChild.Members(courseMembersComponent),
            TabChild.Timetable(courseTimetableComponent)
        )

    val children = defaultTabChildren

    private val sidebarNavigation = OverlayNavigation<SidebarConfig>()
    val childSidebar = childOverlay(
        source = sidebarNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is SidebarConfig.Profile -> SidebarChild.Profile(
                    _profileComponent(onStudyGroupOpen, config.userId, context)
                )
            }
        }
    )

    private val stackNavigation = StackNavigation<Config>()
    val childStack = childStack(
        source = stackNavigation,
        handleBackButton = true,
        initialConfiguration = Config.None,
        childFactory = { config, context ->
            when (config) {
                is Config.Topics -> Child.Topics(
                    _courseTopicsComponent(courseId, context)
                )

                is Config.CourseEditor -> Child.CourseEditor(
                    _courseEditorComponent(stackNavigation::pop, courseId, context)
                )

                is Config.CourseWorkEditor -> Child.CourseWorkEditor(
                    _courseWorkEditorComponent(
                        stackNavigation::pop,
                        courseId,
                        config.workId,
                        null,
                        context
                    )
                )

                is Config.CourseWork -> Child.CourseWork(
                    _courseWorkComponent(
                        { _, workId -> stackNavigation.push(Config.CourseWorkEditor(workId)) },
                        stackNavigation::pop,
                        courseId, config.workId,
                        context
                    )
                )

                Config.None -> Child.None
            }
        }
    )

    fun onAddWorkClick() {
        stackNavigation.push(Config.CourseWorkEditor(null))
    }

    fun onCourseEditClick() {
        stackNavigation.push(Config.CourseEditor)
    }

    fun onOpenTopicsClick() {
        stackNavigation.push(Config.Topics)
    }

    sealed class TabChild(val title: String) {

        class Elements(val component: CourseElementsComponent) : TabChild("Элементы")

        class Members(val component: CourseMembersComponent) : TabChild("Участники")

        class Timetable(val component: CourseTimetableComponent) : TabChild("Расписание")
    }

    @Parcelize
    sealed class Config : Parcelable {

        object None : Config()

        object CourseEditor : Config()

        object Topics : Config()

        data class CourseWork(val workId: UUID) : Config()

        data class CourseWorkEditor(val workId: UUID?) : Config()
    }

    sealed class Child {

        class CourseEditor(val component: CourseEditorComponent) : Child()

        class Topics(val component: CourseTopicsComponent) : Child()

        data class CourseWork(val component: CourseWorkComponent) : Child()

        class CourseWorkEditor(val component: CourseWorkEditorComponent) : Child()

        object None : Child()
    }

    @Parcelize
    sealed class SidebarConfig : Parcelable {

        data class Profile(val userId: UUID) : SidebarConfig()
    }

    sealed class SidebarChild {

        class Profile(val component: ProfileComponent) : SidebarChild()
    }
}