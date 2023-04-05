package com.denchic45.kts.ui.studygroup.users

import androidx.lifecycle.viewModelScope
import com.denchic45.kts.MobileNavigationDirections
import com.denchic45.kts.R
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.model.GroupMembers
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.usecase.*
import com.denchic45.kts.ui.base.BaseViewModel
import com.denchic45.kts.ui.model.*
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named


class GroupMembersViewModel @Inject constructor(
    findGroupMembersUseCase: FindGroupMembersUseCase,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    findSelfUserUseCase: FindSelfUserUseCase,
    private val assignUserRoleInScopeUseCase: AssignUserRoleInScopeUseCase,
    private val removeUserRoleFromScopeUseCase: RemoveUserRoleFromScopeUseCase,
    @Named(GroupMembersFragment.GROUP_ID) _groupId: String,
) : BaseViewModel() {

    val showUserOptions = SingleLiveData<Pair<Int, List<OptionItem>>>()

    private var groupId = _groupId.toUUID()

    private val user = findSelfUserUseCase()

    private val _members: StateFlow<Resource<GroupMembers>> = flow {
        emit(findGroupMembersUseCase(groupId))
    }.mapResource { scopeMembers ->
        val curatorMember = scopeMembers.firstOrNull { member -> Role.Curator in member.roles }
        val groupCurator = curatorMember?.user?.toUserItem()
        val students = (curatorMember?.let { scopeMembers - it }
            ?: scopeMembers).map { it.user.toUserItem() }
        GroupMembers(
            groupId = groupId,
            curator = groupCurator,
            headmanId = scopeMembers.find { member -> Role.Headman in member.roles }?.user?.id,
            students = students
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        Resource.Loading
    )

    val members = _members.mapResource { groupMembers ->
        mutableListOf<UiModel>().apply {
            groupMembers.curator?.let { curator ->
                add(Header("Куратор"))
                add(curator)
            }
            add(Header("Студенты"))
            addAll(groupMembers.students)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading)

    private val capabilities = flow {
        emit(
            checkUserCapabilitiesInScopeUseCase(
                scopeId = groupId,
                capabilities = listOf(Capability.WriteUser)
            )
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading)

    init {
//        uiPermissions.putPermissions(
//            Permission(ALLOW_EDIT_USERS, { isTeacher }, { hasAdminPerms() })
//        )
    }

    fun onMemberClick(position: Int) {
        viewModelScope.launch {
            capabilities.value.onSuccess { capabilities ->
                members.value.onSuccess { members ->
                    val selectedMemberId = members[position].id
                    if (capabilities.hasCapability(Capability.WriteUser)) {
                        showUserOptions.value = position to buildUserOptions(selectedMemberId)
                    }
                }
            }
        }
    }

    private suspend fun buildUserOptions(selectedUserId: UUID): List<OptionItem> {
        return buildList {
            _members.value.onSuccess {
                with(it) {
                    add(
                        if (headmanId == selectedUserId) {
                            OptionItem(
                                id = "OPTION_REMOVE_HEADMAN",
                                title = UiText.IdText(R.string.option_remove_headman)
                            )
                        } else {
                            OptionItem(
                                id = "OPTION_SET_HEADMAN",
                                title = UiText.IdText(R.string.option_set_headman)
                            )
                        }
                    )
                    add(
                        OptionItem(
                            id = "OPTION_EDIT_USER",
                            title = UiText.IdText(R.string.option_edit_user)
                        )
                    )
                    add(
                        OptionItem(
                            id = "OPTION_REMOVE_USER",
                            title = UiText.IdText(R.string.option_remove_user)
                        )
                    )
                }
            }
        }
    }

    fun onUserItemClick(position: Int) {
        members.value.onSuccess {
            navigateTo(MobileNavigationDirections.actionGlobalProfileFragment(it[position].id.toString()))
        }
    }

    fun onOptionUserClick(optionId: String, memberId: UUID) {
        viewModelScope.launch {
            when (optionId) {
                OPTION_SHOW_PROFILE -> {
                }
                OPTION_EDIT_USER -> {
                    navigateTo(
                        MobileNavigationDirections.actionGlobalUserEditorFragment(
                            userId = memberId.toString()
                        )
                    )
                }
                OPTION_SET_HEADMAN -> {
                    assignUserRoleInScopeUseCase(memberId, Role.Headman.id, groupId)
                }
                OPTION_REMOVE_HEADMAN -> {
                    removeUserRoleFromScopeUseCase(memberId, Role.Headman.id, groupId)
                }
            }
        }
    }

    companion object {
        const val ALLOW_EDIT_USERS = "EDIT_USERS"
        const val OPTION_SHOW_PROFILE = "OPTION_SHOW_PROFILE"
        const val OPTION_EDIT_USER = "OPTION_EDIT_USER"
        const val OPTION_SET_HEADMAN = "OPTION_SET_HEADMAN"
        const val OPTION_REMOVE_HEADMAN = "OPTION_REMOVE_HEADMAN"
    }
}
