package com.denchic45.studiversity.ui.studygroupmembers

import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.api.role.model.Capability
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.domain.model.toUserItem
import com.denchic45.studiversity.domain.resource.*
import com.denchic45.studiversity.domain.usecase.*
import com.denchic45.studiversity.ui.membereditor.MemberRolesEditorComponent
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class StudyGroupMembersComponent(
    findStudyGroupMembersUseCase: FindStudyGroupMembersUseCase,
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val assignUserRoleInScopeUseCase: AssignUserRoleInScopeUseCase,
    private val removeUserRoleFromScopeUseCase: RemoveUserRoleFromScopeUseCase,
    private val removeStudyGroupMemberUseCase: RemoveStudyGroupMemberUseCase,
    private val inviteStudyGroupMembersComponent: (
            (Boolean) -> Unit, UUID, ComponentContext
    ) -> InviteStudyGroupMembersComponent,
    private val memberRolesEditorComponent: (
            (Boolean) -> Unit, UUID, UserItem, ComponentContext
    ) -> MemberRolesEditorComponent,
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
                    memberRolesEditorComponent(
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
    val members: MutableStateFlow<Resource<StudyGroupMembersState>> = MutableStateFlow(loadingResource())
    private val observedMembers = findStudyGroupMembersUseCase(studyGroupId)
        .mapResource { scopeMembers ->
            val curators = scopeMembers.filter { member -> Role.Curator in member.roles }
            val students = scopeMembers.filter { member -> Role.StudyGroupStudent in member.roles }
            StudyGroupMembersState(
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

        class MemberEditor(val component: MemberRolesEditorComponent) : SlotChild()
    }
}

@Immutable
data class StudyGroupMembersState(
    val studyGroupId: UUID,
    val curators: List<UserItem>,
    val headmanId: UUID?,
    val students: List<UserItem>,
    val otherMembers: List<UserItem>
) {
    fun isHeadman(memberId: UUID): Boolean {
        return headmanId == memberId
    }

    fun isEmpty(): Boolean {
        return curators.isEmpty() && students.isEmpty() && otherMembers.isEmpty()
    }
}