package com.denchic45.studiversity.ui.studygroup.members

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.data.domain.model.UserRole
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.mapResource
import com.denchic45.studiversity.domain.model.GroupMembers
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.usecase.AssignUserRoleInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindGroupMembersUseCase
import com.denchic45.studiversity.domain.usecase.RemoveUserRoleFromScopeUseCase
import com.denchic45.studiversity.ui.model.MenuAction
import com.denchic45.studiversity.ui.model.toUserItem
import com.denchic45.stuiversity.api.role.model.Role
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*


interface StudyGroupMembersUiLogic {
    val findGroupMembersUseCase: FindGroupMembersUseCase
    val componentContext: ComponentContext
    val assignUserRoleInScopeUseCase: AssignUserRoleInScopeUseCase
    val removeUserRoleFromScopeUseCase: RemoveUserRoleFromScopeUseCase
    val groupId: UUID

    val coroutineScope: CoroutineScope

    val members: StateFlow<Resource<GroupMembers>>

    val memberAction: MutableStateFlow<Pair<List<StudentAction>, UUID>?>
        get() = MutableStateFlow(null)

    fun onMemberSelect(userId: UUID)

    fun onCloseProfileClick()

    fun onExpandMemberAction(memberId: UUID) {
        members.value.onSuccess { members ->
            memberAction.update {
                listOf(
                    if (members.headmanId == memberId) StudentAction.RemoveHeadman
                    else StudentAction.SetHeadman,
                    StudentAction.Edit
                ) to memberId
            }
        }
    }

    fun onClickMemberAction(action: StudentAction) {
        coroutineScope.launch {
            when (action) {
                StudentAction.SetHeadman -> assignUserRoleInScopeUseCase(
                    memberAction.value!!.second,
                    Role.Headman.id,
                    groupId
                )
                StudentAction.RemoveHeadman -> removeUserRoleFromScopeUseCase(
                    groupId,
                    Role.Headman.id,
                    groupId
                )
                StudentAction.Edit -> onMemberEdit(
                    memberAction.value!!.second,
                    UserRole.STUDENT,
                    groupId
                )
            }
        }
    }

    abstract fun onMemberEdit(userId: UUID, role: UserRole, groupId: UUID)

    fun onDismissAction() {
        memberAction.value = null
    }


    enum class StudentAction(
        override val title: String,
        override val iconName: String? = null,
    ) : MenuAction {
        SetHeadman("Назначить старостой"),
        RemoveHeadman("Лишить прав старосты"),
        Edit("Редактировать")
    }
}