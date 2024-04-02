package com.denchic45.studiversity.ui.studygroup

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.api.role.model.Capability
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindStudyGroupByIdUseCase
import com.denchic45.studiversity.ui.navigation.ChildrenContainer
import com.denchic45.studiversity.ui.navigation.isActiveFlow
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.ui.profile.ProfileComponent
import com.denchic45.studiversity.ui.scopemembereditor.ScopeMemberEditorComponent
import com.denchic45.studiversity.ui.studygroup.courses.StudyGroupCoursesComponent
import com.denchic45.studiversity.ui.studygroup.members.StudyGroupMembersComponent
import com.denchic45.studiversity.ui.studygroup.timetable.StudyGroupTimetableComponent
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorComponent
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*


@Inject
class StudyGroupComponent(
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val profileComponent: (RootNavigator, UUID, ComponentContext) -> ProfileComponent,
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
    private val studyGroupId: UUID,
    @Assisted
    private val rootNavigator: RootNavigator,
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

    private val slotNavigation = SlotNavigation<OverlayConfig>()

    val childSidebar = childSlot(
        source = slotNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is OverlayConfig.StudyGroupEditor -> {
                    SlotChild.StudyGroupEditor(
                        studyGroupEditorComponent(
                            slotNavigation::dismiss,
                            config.studyGroupId,
                            context
                        )
                    )
                }

                is OverlayConfig.Member -> {
                    SlotChild.Member(
                        profileComponent(
                            rootNavigator,
                            config.memberId,
                            context
                        )
                    )
                }

                is OverlayConfig.ScopeMemberEditor -> {
                    SlotChild.ScopeMemberEditor(
                        scopeMemberEditorComponent(
                            listOf(Role.Curator, Role.Student, Role.Headman),
                            studyGroupId,
                            config.memberId,
                            { slotNavigation.dismiss() },
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
                { slotNavigation.activate(OverlayConfig.Member(it)) },
                { slotNavigation.activate(OverlayConfig.ScopeMemberEditor(it)) },
                studyGroupId,
                componentContext.childContext("Members")
            )
        ),
        TabChild.Courses(
            studyGroupCoursesComponent(
                { rootNavigator.bringToFront(RootConfig.Course(it)) },
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

    fun onTabSelect(position: Int) {
        selectedTab.value = position
    }

    fun onEditClick() {
        slotNavigation.activate(OverlayConfig.StudyGroupEditor(studyGroupId))
    }

    fun onAddMemberClick() {
        slotNavigation.activate(OverlayConfig.ScopeMemberEditor(null))
    }

    fun onSidebarClose() {
        slotNavigation.dismiss()
    }

    @Parcelize
    sealed class OverlayConfig : Parcelable {
        data class StudyGroupEditor(val studyGroupId: UUID) : OverlayConfig()

        data class Member(val memberId: UUID) : OverlayConfig()

        data class ScopeMemberEditor(val memberId: UUID?) : OverlayConfig()
    }

    sealed class SlotChild {

        class StudyGroupEditor(val component: StudyGroupEditorComponent) : SlotChild()

        class Member(val component: ProfileComponent) : SlotChild()

        class ScopeMemberEditor(val component: ScopeMemberEditorComponent) : SlotChild()
    }

    sealed class TabChild(val title: String) {
        class Members(val component: StudyGroupMembersComponent) : TabChild("Участники")
        class Courses(val component: StudyGroupCoursesComponent) : TabChild("Курсы")
        class Timetable(val component: StudyGroupTimetableComponent) : TabChild("Расписание")
    }

    override fun hasChildrenFlow(): Flow<Boolean> {
        return childSidebar.isActiveFlow()
    }

    fun onBack() {
        rootNavigator.pop()
    }
}