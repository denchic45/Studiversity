package com.denchic45.studiversity.ui.studygroup.members

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.api.role.model.Capability
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.domain.model.GroupMembers
import com.denchic45.studiversity.domain.model.toUserItem
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.*
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class StudyGroupMembersComponent(
    findGroupMembersUseCase: FindGroupMembersUseCase,
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val assignUserRoleInScopeUseCase: AssignUserRoleInScopeUseCase,
    private val removeUserRoleFromScopeUseCase: RemoveUserRoleFromScopeUseCase,
    private val removeMemberFromScopeUseCase: RemoveMemberFromScopeUseCase,
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    @Assisted
    private val onMemberOpen: (memberId: UUID) -> Unit,
    @Assisted
    private val onMemberEdit: (memberId: UUID) -> Unit,
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

    val members: StateFlow<Resource<GroupMembers>> = flow {
        emit(findGroupMembersUseCase(studyGroupId))
    }.mapResource { scopeMembers ->
        val curatorMember = scopeMembers.firstOrNull { member -> Role.Curator in member.roles }
        val groupCurator = curatorMember?.user?.toUserItem()
        val students = (curatorMember?.let { scopeMembers - it }
            ?: scopeMembers).map {
            it.user.toUserItem(if (it.roles.contains(Role.Headman)) "Староста" else null)
        }
        GroupMembers(
            groupId = studyGroupId,
            curator = groupCurator,
            headmanId = scopeMembers.find { member -> Role.Headman in member.roles }?.user?.id,
            students = students
        )
    }.stateInResource(componentScope)

    val memberActions = MutableStateFlow<Pair<List<MemberAction>, UUID>?>(null)

    fun onMemberClick(memberId: UUID) {
        onMemberOpen(memberId)
    }

    fun onEditMemberClick(memberId: UUID) {
        onMemberEdit(memberId)
    }


    fun onRemoveMemberClick(memberId: UUID) {
        componentScope.launch {
            if (confirmDialogInteractor.confirmRequest(
                    ConfirmState(
                        uiTextOf("Удалить члена группы?"),
                        uiTextOf("Удалятся все данные, связанные с данным пользователем в группе")
                    )
                )
            ) {
                removeMemberFromScopeUseCase(memberId, studyGroupId)
            }
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

    fun onMemberActionsExpand(memberId: UUID) {
        members.value.onSuccess { members ->
            memberActions.update {
                buildList {
                    if (members.students.any { it.id == memberId }) {
                        add(
                            if (members.headmanId != memberId) MemberAction.SET_HEADMAN
                            else MemberAction.REMOVE_HEADMAN
                        )
                    }
                    add(MemberAction.EDIT)
                    add(MemberAction.REMOVE)
                } to memberId
            }
        }
    }

    fun onDismissActions() {
        memberActions.value = null
    }

    enum class MemberAction { EDIT, REMOVE, SET_HEADMAN, REMOVE_HEADMAN }
}
