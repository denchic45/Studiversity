package com.denchic45.studiversity.ui.studygroup

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.api.role.model.Capability
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.domain.model.StudyGroupMembers
import com.denchic45.studiversity.domain.model.toUserItem
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.*
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class StudyGroupMembersComponent(
    findStudyGroupMembersUseCase: FindStudyGroupMembersUseCase,
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val inviteStudyGroupMembersComponent: (
            () -> Unit, UUID, ComponentContext
    ) -> InviteStudyGroupMembersComponent,
    private val studyGroupMemberEditorComponent: (
            () -> Unit, UUID, UUID, ComponentContext
    ) -> StudyGroupMemberEditorComponent,
    private val assignUserRoleInScopeUseCase: AssignUserRoleInScopeUseCase,
    private val removeUserRoleFromScopeUseCase: RemoveUserRoleFromScopeUseCase,
    private val removeMemberFromScopeUseCase: RemoveStudyGroupMemberUseCase,
    @Assisted
    private val rootNavigator: RootNavigator,
    @Assisted
    private val studyGroupId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val checkUserCapabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = studyGroupId,
        capabilities = listOf(Capability.WriteStudyGroup)
    ).shareIn(componentScope, SharingStarted.Lazily)

    val allowEditMembers = checkUserCapabilities
        .mapResource { it.hasCapability(Capability.WriteStudyGroup) }
        .stateInResource(componentScope)

    private val slotNavigation = SlotNavigation<SlotConfig>()
    val childSlot: Value<ChildSlot<SlotConfig, SlotChild>> = childSlot(
        source = slotNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is SlotConfig.InviteMembers -> SlotChild.InviteMembers(
                    inviteStudyGroupMembersComponent(slotNavigation::dismiss, studyGroupId, context)
                )


                is SlotConfig.MemberEditor -> SlotChild.MemberEditor(
                    studyGroupMemberEditorComponent(slotNavigation::dismiss, studyGroupId, config.userId, context)
                )
            }
        }
    )

    val members: StateFlow<Resource<StudyGroupMembers>> = flow {
        emit(findStudyGroupMembersUseCase(studyGroupId))
    }.mapResource { scopeMembers ->
        val curators = scopeMembers.filter { member -> Role.Curator in member.roles }
        val students = scopeMembers.filter { member -> Role.Student in member.roles }
        StudyGroupMembers(
            studyGroupId = studyGroupId,
            curators = curators.map { it.user.toUserItem() },
            headmanId = scopeMembers.find { member -> Role.Headman in member.roles }?.user?.id,
            students = students.map { it.user.toUserItem(if (it.roles.contains(Role.Headman)) "Староста" else null) },
            otherMembers = (scopeMembers - curators.toSet() - students.toSet()).map { it.user.toUserItem() }
        )
    }.stateInResource(componentScope)

    fun onMemberClick(memberId: UUID) {
        rootNavigator.push(RootConfig.Profile(memberId))
    }

    fun onAddMemberClick() {
        slotNavigation.activate(SlotConfig.InviteMembers)
    }

    fun onEditMemberClick(memberId: UUID) {
        slotNavigation.activate(SlotConfig.MemberEditor(memberId))
    }


    fun onRemoveMemberClick(memberId: UUID) {
        componentScope.launch {
            removeMemberFromScopeUseCase(memberId, studyGroupId)
        }
    }

    fun onMemberSetHeadmanClick(memberId: UUID) {
        componentScope.launch {
            assignUserRoleInScopeUseCase(
                memberId,
                Role.Headman.id,
                studyGroupId
            )
        }
    }

    fun onRemoveHeadmanClick(memberId: UUID) {
        componentScope.launch {
            removeUserRoleFromScopeUseCase(
                memberId,
                Role.Headman.id,
                studyGroupId
            )
        }
    }

//    fun onMemberActionsExpand(memberId: UUID) {
//        members.value.onSuccess { members ->
//            memberActions.update {
//                buildList {
//                    if (members.students.any { it.id == memberId }) {
//                        add(
//                            if (members.headmanId != memberId) MemberAction.SET_HEADMAN
//                            else MemberAction.REMOVE_HEADMAN
//                        )
//                    }
//                    add(MemberAction.EDIT)
//                    add(MemberAction.REMOVE)
//                } to memberId
//            }
//        }
//    }

//    fun onDismissActions() {
//        memberActions.value = null
//    }

    @Parcelize
    sealed class SlotConfig : Parcelable {
        data object InviteMembers : SlotConfig() {
            private fun readResolve(): Any = InviteMembers
        }

        data class MemberEditor(val userId: UUID) : SlotConfig()
    }

    sealed class SlotChild {
        class InviteMembers(val component: InviteStudyGroupMembersComponent) : SlotChild()

        class MemberEditor(val component: StudyGroupMemberEditorComponent) : SlotChild()
    }

//    enum class MemberAction { EDIT, REMOVE, SET_HEADMAN, REMOVE_HEADMAN }
}
