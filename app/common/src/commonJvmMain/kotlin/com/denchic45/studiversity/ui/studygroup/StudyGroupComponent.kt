package com.denchic45.studiversity.ui.studygroup

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.mapResource
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindStudyGroupByIdUseCase
import com.denchic45.studiversity.ui.navigation.ChildrenContainer
import com.denchic45.studiversity.ui.navigation.isActiveFlow
import com.denchic45.studiversity.ui.profile.ProfileComponent
import com.denchic45.studiversity.ui.scopemembereditor.ScopeMemberEditorComponent
import com.denchic45.studiversity.ui.studygroup.courses.StudyGroupCoursesComponent
import com.denchic45.studiversity.ui.studygroup.members.StudyGroupMembersComponent
import com.denchic45.studiversity.ui.studygroup.timetable.StudyGroupTimetableComponent
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorComponent
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.role.model.Role
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID


@Inject
class StudyGroupComponent(
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val profileComponent: (onStudyGroupOpen: (UUID) -> Unit, UUID, ComponentContext) -> ProfileComponent,
    scopeMemberEditorComponent: (
        availableRoles: List<Role>,
        scopeId: UUID,
        memberId: UUID?,
        onFinish: () -> Unit,
        ComponentContext
    ) -> ScopeMemberEditorComponent,
    private val studyGroupEditorComponent: (
        onFinish: () -> Unit,
        UUID?,
        ComponentContext,
    ) -> StudyGroupEditorComponent,
    studyGroupMembersComponent: (
        onMemberOpen: (UUID) -> Unit,
        onMemberEdit: (UUID) -> Unit,
        UUID,
        ComponentContext,
    ) -> StudyGroupMembersComponent,
    studyGroupCoursesComponent: (
        onCourseOpen: (UUID) -> Unit,
        UUID,
        ComponentContext,
    ) -> StudyGroupCoursesComponent,
    studyGroupTimetableComponent: (UUID, ComponentContext) -> StudyGroupTimetableComponent,
    findStudyGroupByIdUseCase: FindStudyGroupByIdUseCase,
    @Assisted
    private val onCourseOpen: (UUID) -> Unit,
    @Assisted
    private val onStudyGroupOpen: (UUID) -> Unit,
    @Assisted
    private val studyGroupId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext, ChildrenContainer {

    private val componentScope = componentScope()

    private val checkUserCapabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = studyGroupId,
        capabilities = listOf(Capability.WriteStudyGroup)
    ).shareIn(componentScope, SharingStarted.Lazily)

    val allowEdit = checkUserCapabilities
        .mapResource { it.hasCapability(Capability.WriteStudyGroup) }
        .stateInResource(componentScope)

//    val appBarState = MutableStateFlow(
//        AppBarState(
//            title = uiTextOf("Ваши группы"),
//            onDropdownMenuItemClick = {
//                it.title.onString { action ->
//                    when (action) {
//                        "Редактировать" -> {
//                            selectedStudyGroup.value.onSuccess {
//                                onStudyGroupEditClick(it.id)
//                            }
//                        }
//                    }
//                }
//            })
//    )

    private val sidebarNavigation = OverlayNavigation<OverlayConfig>()

    val childSidebar = childOverlay(
        source = sidebarNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is OverlayConfig.StudyGroupEditor -> {
                    OverlayChild.StudyGroupEditor(
                        studyGroupEditorComponent(
                            sidebarNavigation::dismiss,
                            config.studyGroupId,
                            context
                        )
                    )
                }

                is OverlayConfig.Member -> {
                    OverlayChild.Member(
                        profileComponent(
                            onStudyGroupOpen,
                            config.memberId,
                            context
                        )
                    )
                }

                is OverlayConfig.ScopeMemberEditor -> {
                    OverlayChild.ScopeMemberEditor(
                        scopeMemberEditorComponent(
                            listOf(Role.Curator, Role.Student, Role.Headman),
                            studyGroupId,
                            config.memberId,
                            { sidebarNavigation.dismiss() },
                            context
                        )
                    )
                }
            }
        }
    )

    val childTabs = listOf(
        TabChild.Members(
            studyGroupMembersComponent(
                { sidebarNavigation.activate(OverlayConfig.Member(it)) },
                { sidebarNavigation.activate(OverlayConfig.ScopeMemberEditor(it)) },
                studyGroupId,
                componentContext.childContext("Members")
            )
        ),
        TabChild.Courses(
            studyGroupCoursesComponent(
                onCourseOpen,
                studyGroupId,
                componentContext.childContext("Courses")
            )
        ),
        TabChild.Timetable(
            studyGroupTimetableComponent(
                studyGroupId,
                componentContext.childContext("Timetable")
            )
        )
    )

    val studyGroup = findStudyGroupByIdUseCase(studyGroupId).stateInResource(componentScope)

    val selectedTab = MutableStateFlow(0)

//    init {
//        lifecycle.subscribe(onResume = {
//            selectedStudyGroup.onEach { resource ->
//                resource.onSuccess { selectedStudyGroup ->
//                    appBarState.update {
//                        it.copy(
//                            title = uiTextOf(selectedStudyGroup.name),
//                            dropdown = resource.let {
//                                listOf(DropdownMenuItem("edit", uiTextOf("Редактировать")))
//                            },
//                            onDropdownMenuItemClick = {
//                                when (it.id) {
//                                    "edit" -> componentScope.launch {
//                                        selectedStudyGroup.id.toString()
//                                    }
//                                }
//                            })
//                    }
//                }
//            }.launchIn(componentScope)
//        })
//    }

    fun onTabSelect(position: Int) {
        selectedTab.value = position
    }

    fun onEditClick() {
        sidebarNavigation.activate(OverlayConfig.StudyGroupEditor(studyGroupId))
    }

    fun onAddMemberClick() {
        sidebarNavigation.activate(OverlayConfig.ScopeMemberEditor(null))
    }

    fun onSidebarClose() {
        sidebarNavigation.dismiss()
    }

    @Parcelize
    sealed class OverlayConfig : Parcelable {
        data class StudyGroupEditor(val studyGroupId: UUID) : OverlayConfig()

        data class Member(val memberId: UUID) : OverlayConfig()

        data class ScopeMemberEditor(val memberId: UUID?) : OverlayConfig()
    }

    sealed class OverlayChild {

        class StudyGroupEditor(val component: StudyGroupEditorComponent) : OverlayChild()

        class Member(val component: ProfileComponent) : OverlayChild()

        class ScopeMemberEditor(val component: ScopeMemberEditorComponent) : OverlayChild()
    }

    sealed class TabChild(val title: String) {
        class Members(val component: StudyGroupMembersComponent) : TabChild("Участники")
        class Courses(val component: StudyGroupCoursesComponent) : TabChild("Курсы")
        class Timetable(val component: StudyGroupTimetableComponent) : TabChild("Расписание")
    }

    override fun hasChildrenFlow(): Flow<Boolean> {
        return childSidebar.isActiveFlow()
    }
}