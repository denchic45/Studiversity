package com.denchic45.studiversity.ui.course

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindCourseByIdUseCase
import com.denchic45.studiversity.ui.CourseMembersComponent
import com.denchic45.studiversity.ui.courseeditor.CourseEditorComponent
import com.denchic45.studiversity.ui.courseelements.CourseElementsComponent
import com.denchic45.studiversity.ui.coursematerial.CourseMaterialComponent
import com.denchic45.studiversity.ui.coursematerialeditor.CourseMaterialEditorComponent
import com.denchic45.studiversity.ui.coursestudygroups.CourseStudyGroupsComponent
import com.denchic45.studiversity.ui.coursetimetable.CourseTimetableComponent
import com.denchic45.studiversity.ui.coursetopics.CourseTopicsComponent
import com.denchic45.studiversity.ui.coursework.CourseWorkComponent
import com.denchic45.studiversity.ui.courseworkeditor.CourseWorkEditorComponent
import com.denchic45.studiversity.ui.navigation.ChildrenContainer
import com.denchic45.studiversity.ui.navigation.isActiveFlow
import com.denchic45.studiversity.ui.profile.ProfileComponent
import com.denchic45.studiversity.ui.scopemembereditor.ScopeMemberEditorComponent
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.role.model.Role
import kotlinx.coroutines.flow.Flow
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
        onFinish: () -> Unit,
        courseId: UUID,
        materialId: UUID?,
        topicId: UUID?,
        ComponentContext,
    ) -> CourseMaterialEditorComponent,
    courseStudyGroupsComponent: (UUID, ComponentContext) -> CourseStudyGroupsComponent,
    _courseElementsComponent: (
        courseId: UUID,
        onElementOpen: (courseId: UUID, elementId: UUID, type: CourseElementType) -> Unit,
        ComponentContext,
    ) -> CourseElementsComponent,
    _courseMembersComponent: (
        courseId: UUID,
        onMemberOpen: (memberId: UUID) -> Unit,
        onMemberEdit: (memberId: UUID) -> Unit,
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
    scopeMemberEditorComponent: (
        availableRoles: List<Role>,
        scopeId: UUID,
        memberId: UUID?,
        onFinish: () -> Unit,
        ComponentContext
    ) -> ScopeMemberEditorComponent,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val onStudyGroupOpen: (studyGroupId: UUID) -> Unit,
    @Assisted
    private val courseId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext, ChildrenContainer {
    private val componentScope = componentScope()

    val course = flow { emit(findCourseByIdUseCase(courseId)) }.stateInResource(componentScope)

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

    private val courseElementsComponent = _courseElementsComponent(
        courseId,
        { _, elementId, type ->
            stackNavigation.push(
               when(type) {
                   CourseElementType.WORK ->  Config.CourseWork(elementId)
                   CourseElementType.MATERIAL ->  Config.CourseMaterial(elementId)
               }
            )
        },
        componentContext.childContext("Elements")
    )

    private val courseMembersComponent = _courseMembersComponent(
        courseId,
        { sidebarNavigation.activate(SidebarConfig.Profile(it)) },
        { sidebarNavigation.activate(SidebarConfig.ScopeMemberEditor(it)) },
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

                is SidebarConfig.ScopeMemberEditor -> SidebarChild.ScopeMemberEditor(
                    scopeMemberEditorComponent(
                        listOf(Role.Student, Role.Teacher),
                        courseId,
                        config.memberId,
                        sidebarNavigation::dismiss,
                        context
                    )
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
                    _courseWorkComponent(
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
                                Config.CourseMaterialEditor(
                                    materialId
                                )
                            )
                        },
                        courseId,
                        config.materialId,
                        context
                    )
                )

                is Config.CourseMaterialEditor -> Child.CourseWorMaterialEditor(
                    courseMaterialEditorComponent(
                        stackNavigation::pop,
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

    fun onAddMemberClick() {
        sidebarNavigation.activate(SidebarConfig.ScopeMemberEditor(null))
    }

    fun onAddWorkClick() {
        stackNavigation.push(Config.CourseWorkEditor(null))
    }

    fun onAddMaterialClick() {
        stackNavigation.push(Config.CourseMaterialEditor(null))
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

        object None : Config()

        object CourseEditor : Config()

        object Topics : Config()

        object CourseStudyGroupsEditor : Config()

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

        object None : Child()
    }

    @Parcelize
    sealed class SidebarConfig : Parcelable {

        data class Profile(val userId: UUID) : SidebarConfig()

        data class ScopeMemberEditor(val memberId: UUID?) : SidebarConfig()
    }

    sealed class SidebarChild {

        class Profile(val component: ProfileComponent) : SidebarChild()

        class ScopeMemberEditor(val component: ScopeMemberEditorComponent) : SidebarChild()
    }

    override fun hasChildrenFlow(): Flow<Boolean> {
        return childSidebar.isActiveFlow()
    }

    fun onCloseClick() {
        onFinish()
    }
}