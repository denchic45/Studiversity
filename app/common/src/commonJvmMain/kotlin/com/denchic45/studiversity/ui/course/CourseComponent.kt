package com.denchic45.studiversity.ui.course

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.api.course.element.model.CourseElementType
import com.denchic45.studiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.studiversity.api.role.model.Capability
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindCourseByIdUseCase
import com.denchic45.studiversity.ui.courseeditor.CourseEditorComponent
import com.denchic45.studiversity.ui.courseelements.CourseElementsComponent
import com.denchic45.studiversity.ui.coursematerial.CourseMaterialComponent
import com.denchic45.studiversity.ui.coursematerialeditor.CourseMaterialEditorComponent
import com.denchic45.studiversity.ui.coursemembers.CourseMembersComponent
import com.denchic45.studiversity.ui.coursestudygroups.CourseStudyGroupsComponent
import com.denchic45.studiversity.ui.coursetimetable.CourseTimetableComponent
import com.denchic45.studiversity.ui.coursetopics.CourseTopicsComponent
import com.denchic45.studiversity.ui.coursework.CourseWorkComponent
import com.denchic45.studiversity.ui.courseworkeditor.CourseWorkEditorComponent
import com.denchic45.studiversity.ui.navigation.ChildrenContainer
import com.denchic45.studiversity.ui.navigation.isActiveFlow
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID


@Inject
class CourseComponent(
    findCourseByIdUseCase: FindCourseByIdUseCase,
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    courseTopicsComponent: (
        courseId: UUID,
        ComponentContext,
    ) -> CourseTopicsComponent,
    courseEditorComponent: (
        onFinish: (Boolean) -> Unit,
        courseId: UUID?,
        ComponentContext,
    ) -> CourseEditorComponent,
    courseWorkComponent: (
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
    _courseMaterialComponent: (
        onFinish: () -> Unit,
        onEdit: (courseId: UUID, elementId: UUID?) -> Unit,
        courseId: UUID,
        materialId: UUID,
        ComponentContext,
    ) -> CourseMaterialComponent,
    courseMaterialEditorComponent: (
        onFinish: (CourseMaterialResponse) -> Unit,
        courseId: UUID,
        materialId: UUID?,
        topicId: UUID?,
        ComponentContext,
    ) -> CourseMaterialEditorComponent,
    courseStudyGroupsComponent: (UUID, ComponentContext) -> CourseStudyGroupsComponent,
    courseElementsComponent: (
        courseId: UUID,
        onElementOpen: (courseId: UUID, elementId: UUID, type: CourseElementType) -> Unit,
        ComponentContext,
    ) -> CourseElementsComponent,
    courseMembersComponent: (
        courseId: UUID,
        onMemberOpen: (memberId: UUID) -> Unit,
        onMemberEdit: (memberId: UUID) -> Unit,
        ComponentContext,
    ) -> CourseMembersComponent,
    courseTimetableComponent: (
        courseId: UUID,
        ComponentContext,
    ) -> CourseTimetableComponent,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val rootNavigator: RootNavigator,
    @Assisted
    private val courseId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext, ChildrenContainer {
    private val componentScope = componentScope()

    private val trigger = Channel<Unit>()
    val course = trigger.receiveAsFlow()
        .onStart { emit(Unit) }
        .map { findCourseByIdUseCase(courseId) }
        .stateInResource(componentScope)

    private val checkCapabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = courseId,
        capabilities = listOf(Capability.WriteCourse)
    ).stateInResource(componentScope)

    val allowEdit = checkCapabilities.map {
        when (val resource = it) {
            Resource.Loading,
            is Resource.Error,
            -> false

            is Resource.Success -> {
                resource.value.hasCapability(Capability.WriteCourse)
            }
        }
    }

    private val courseElementsComponent = courseElementsComponent(
        courseId,
        { _, elementId, type ->
            stackNavigation.push(
                when (type) {
                    CourseElementType.WORK -> Config.CourseWork(elementId)
                    CourseElementType.MATERIAL -> Config.CourseMaterial(elementId)
                }
            )
        },
        componentContext.childContext("Elements")
    )

    private val courseMembersComponent = courseMembersComponent(
        courseId,
        { rootNavigator.bringToFront(RootConfig.Profile(it)) },
        { sidebarNavigation.activate(SidebarConfig.ScopeMemberEditor(it)) },
        componentContext.childContext("Members")
    )

    private val courseTimetableComponent = courseTimetableComponent(
        courseId,
        componentContext.childContext("Timetable")
    )

    val capabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = courseId,
        capabilities = listOf()
    ).shareIn(componentScope, SharingStarted.Lazily)

    private val defaultTabChildren = listOf(
        TabChild.Elements(this.courseElementsComponent),
        TabChild.Members(this.courseMembersComponent),
        TabChild.Timetable(this.courseTimetableComponent)
    )

    val children = defaultTabChildren

    private val sidebarNavigation = SlotNavigation<SidebarConfig>()
    val childSidebar = childSlot(
        source = sidebarNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
//                is SidebarConfig.Profile -> SidebarChild.Profile(
//                    _profileComponent(onStudyGroupOpen, config.userId, context)
//                )

//                is SidebarConfig.ScopeMemberEditor -> SidebarChild.ScopeMemberEditor(
//                    scopeMemberEditorComponent(
//                        listOf(Role.Student, Role.Teacher),
//                        courseId,
//                        config.memberId,
//                        sidebarNavigation::dismiss,
//                        context
//                    )
//                )
                is SidebarConfig.ScopeMemberEditor -> TODO()
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
                    courseTopicsComponent(courseId, context)
                )

                is Config.CourseEditor -> Child.CourseEditor(
                    courseEditorComponent({ edit ->
                        stackNavigation.pop()
                        if (edit) forceUpdate()
                    }, courseId, context)
                )

                Config.CourseStudyGroupsEditor -> Child.CourseStudyGroupsEditor(
                    courseStudyGroupsComponent(courseId, context)
                )

                is Config.CourseWorkEditor -> Child.CourseWorkEditor(
                    courseWorkEditorComponent(
                        stackNavigation::pop,
                        courseId,
                        config.workId,
                        null,
                        context
                    )
                )

                is Config.CourseWork -> Child.CourseWork(
                    courseWorkComponent(
                        { _, workId -> stackNavigation.push(Config.CourseWorkEditor(workId)) },
                        stackNavigation::pop,
                        courseId, config.workId,
                        context
                    )
                )

                is Config.CourseMaterial -> Child.CourseMaterial(
                    _courseMaterialComponent(
                        stackNavigation::pop,
                        { courseId, materialId ->
                            stackNavigation.bringToFront(
                                Config.CourseMaterialEditor(materialId)
                            )
                        },
                        courseId,
                        config.materialId,
                        context
                    )
                )

                is Config.CourseMaterialEditor -> Child.CourseWorMaterialEditor(
                    courseMaterialEditorComponent(
                        { stackNavigation.pop() },
                        courseId,
                        config.materialId,
                        null,
                        context
                    )
                )

                Config.None -> Child.None
            }
        }
    )

    private fun forceUpdate() {
        componentScope.launch { trigger.send(Unit) }
    }

    fun onAddMemberClick() {
        sidebarNavigation.activate(SidebarConfig.ScopeMemberEditor(null))
    }

    fun onAddWorkClick() {
        stackNavigation.push(Config.CourseWorkEditor(null))
    }

    fun onAddMaterialClick() {
        stackNavigation.push(
            Config.CourseMaterialEditor(null)
        )
    }

    fun onCourseEditClick() {
        stackNavigation.push(Config.CourseEditor)
    }

    fun onStudyGroupsEditClick() {
        stackNavigation.push(Config.CourseStudyGroupsEditor)
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

        data object None : Config() {
            private fun readResolve(): Any = None
        }

        data object CourseEditor : Config() {
            private fun readResolve(): Any = CourseEditor
        }

        data object Topics : Config() {
            private fun readResolve(): Any = Topics
        }

        data object CourseStudyGroupsEditor : Config() {
            private fun readResolve(): Any = CourseStudyGroupsEditor
        }

        data class CourseWork(val workId: UUID) : Config()

        data class CourseWorkEditor(val workId: UUID?) : Config()

        data class CourseMaterial(val materialId: UUID) : Config()

        data class CourseMaterialEditor(val materialId: UUID?) : Config()
    }

    sealed class Child {

        class CourseEditor(val component: CourseEditorComponent) : Child()

        class Topics(val component: CourseTopicsComponent) : Child()

        class CourseStudyGroupsEditor(val component: CourseStudyGroupsComponent) : Child()

        data class CourseWork(val component: CourseWorkComponent) : Child()

        class CourseWorkEditor(val component: CourseWorkEditorComponent) : Child()

        data class CourseMaterial(val component: CourseMaterialComponent) : Child()

        class CourseWorMaterialEditor(val component: CourseMaterialEditorComponent) : Child()

        data object None : Child()
    }

    @Parcelize
    sealed class SidebarConfig : Parcelable {

//        data class Profile(val userId: UUID) : SidebarConfig()

        data class ScopeMemberEditor(val memberId: UUID?) : SidebarConfig()
    }

    sealed class SidebarChild {

//        class Profile(val component: ProfileComponent) : SidebarChild()

//        class ScopeMemberEditor(val component: ScopeMemberEditorComponent) : SidebarChild()
    }

    override fun hasChildrenFlow(): Flow<Boolean> {
        return childSidebar.isActiveFlow()
    }

    fun onCloseClick() {
        onFinish()
    }
}