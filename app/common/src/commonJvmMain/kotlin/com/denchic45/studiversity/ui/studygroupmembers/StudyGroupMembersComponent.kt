package com.denchic45.studiversity.ui.studygroupmembers

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.api.role.model.Capability
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.domain.model.StudyGroupMembers
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.domain.model.toUserItem
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.loadingResource
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.AssignUserRoleInScopeUseCase
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindStudyGroupMembersUseCase
import com.denchic45.studiversity.domain.usecase.RemoveStudyGroupMemberUseCase
import com.denchic45.studiversity.domain.usecase.RemoveUserRoleFromScopeUseCase
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class StudyGroupMembersComponent(
    findStudyGroupMembersUseCase: FindStudyGroupMembersUseCase,
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val inviteStudyGroupMembersComponent: (
            (Boolean) -> Unit, UUID, ComponentContext
    ) -> InviteStudyGroupMembersComponent,
    private val studyGroupMemberEditorComponent: (
            (Boolean) -> Unit, UUID, UserItem, ComponentContext
    ) -> StudyGroupMemberEditorComponent,
    private val assignUserRoleInScopeUseCase: AssignUserRoleInScopeUseCase,
    private val removeUserRoleFromScopeUseCase: RemoveUserRoleFromScopeUseCase,
    private val removeStudyGroupMemberUseCase: RemoveStudyGroupMemberUseCase,
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
                    inviteStudyGroupMembersComponent(
                        { invited ->
                            slotNavigation.dismiss()
                            if (invited) forceUpdate()
                        }, studyGroupId,
                        context
                    )
                )

                is SlotConfig.MemberEditor -> SlotChild.MemberEditor(
                    studyGroupMemberEditorComponent(
                        { edited ->
                            slotNavigation.dismiss()
                            if (edited) forceUpdate()
                        },
                        studyGroupId,
                        config.user,
                        context
                    )
                )
            }
        }
    )

    val refreshing = MutableStateFlow(false)
    val members: MutableStateFlow<Resource<StudyGroupMembers>> = MutableStateFlow(loadingResource())
    private val observedMembers = findStudyGroupMembersUseCase(studyGroupId)
        .mapResource { scopeMembers ->
            val curators = scopeMembers.filter { member -> Role.Curator in member.roles }
            val students = scopeMembers.filter { member -> Role.StudyGroupStudent in member.roles }
            StudyGroupMembers(
                studyGroupId = studyGroupId,
                curators = curators.map { it.user.toUserItem() },
                headmanId = scopeMembers.find { member -> Role.Headman in member.roles }?.user?.id,
                students = students.map { it.user.toUserItem(if (it.roles.contains(Role.Headman)) "Староста" else null) },
                otherMembers = (scopeMembers - curators.toSet() - students.toSet()).map { it.user.toUserItem() }
            )
        }

    init {
        componentScope.launch { members.emitAll(observedMembers) }

        refreshing.filter { it }.onEach { forceUpdate() }
            .launchIn(componentScope)

        members.drop(1).onEach { refreshing.value = false }
            .launchIn(componentScope)
    }

    private fun forceUpdate() {
        componentScope.launch { members.value = observedMembers.last() }
    }

    fun onMemberClick(memberId: UUID) {
        rootNavigator.push(RootConfig.Profile(memberId))
    }

    fun onAddMemberClick() {
        slotNavigation.activate(SlotConfig.InviteMembers)
    }

    fun onEditMemberClick(member: UserItem) {
        slotNavigation.activate(SlotConfig.MemberEditor(member))
    }


    fun onRemoveMemberClick(memberId: UUID) {
        componentScope.launch {
            removeStudyGroupMemberUseCase(studyGroupId, memberId)
                .onSuccess { forceUpdate() }
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

    @Parcelize
    sealed class SlotConfig : Parcelable {
        data object InviteMembers : SlotConfig() {
            private fun readResolve(): Any = InviteMembers
        }

        data class MemberEditor(val user: UserItem) : SlotConfig()
    }

    sealed class SlotChild {
        class InviteMembers(val component: InviteStudyGroupMembersComponent) : SlotChild()

        class MemberEditor(val component: StudyGroupMemberEditorComponent) : SlotChild()
    }
}