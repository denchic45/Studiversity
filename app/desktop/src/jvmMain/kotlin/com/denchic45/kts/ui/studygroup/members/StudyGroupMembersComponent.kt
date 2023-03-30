package com.denchic45.kts.ui.studygroup.members

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.model.GroupMembers
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.usecase.AssignUserRoleInScopeUseCase
import com.denchic45.kts.domain.usecase.FindGroupMembersUseCase
import com.denchic45.kts.domain.usecase.RemoveUserRoleFromScopeUseCase
import com.denchic45.kts.ui.model.MenuAction
import com.denchic45.kts.ui.model.toUserItem
import com.denchic45.kts.ui.navigation.*
import com.denchic45.kts.ui.profile.ProfileComponent
import com.denchic45.kts.ui.usereditor.UserEditorComponent
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.role.model.Role
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class StudyGroupMembersComponent(
    findGroupMembersUseCase: FindGroupMembersUseCase,
    componentContext: ComponentContext,
    private val assignUserRoleInScopeUseCase: AssignUserRoleInScopeUseCase,
    private val removeUserRoleFromScopeUseCase: RemoveUserRoleFromScopeUseCase,
    profileComponent: (userId: UUID) -> ProfileComponent,
    userEditorComponent: (onFinish: () -> Unit, config: UserEditorConfig) -> UserEditorComponent,
    private val groupId: UUID,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<GroupMembersConfig>()

    val stack: Value<ChildStack<GroupMembersConfig, GroupMembersChild>> = childStack(
        source = navigation,
        initialConfiguration = GroupMembersConfig.Unselected,
        childFactory = { config, _ ->
            when (config) {
                GroupMembersConfig.Unselected -> GroupMembersChild.Unselected
                is ProfileConfig -> ProfileChild(profileComponent(config.userId))
                is UserEditorConfig -> UserEditorChild(
                    userEditorComponent(navigation::pop, config)
                )
            }
        })

    private val componentScope = componentScope()

    val members: StateFlow<Resource<GroupMembers>> = flow {
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
        componentScope,
        SharingStarted.Lazily,
        Resource.Loading
    )

    val selectedMember = MutableStateFlow<UUID?>(null)

    val memberAction = MutableStateFlow<Pair<List<StudentAction>, UUID>?>(null)

    init {
        componentScope.launch {
            selectedMember.collect { userId ->
                val config = if (userId != null) ProfileConfig(userId)
                else GroupMembersConfig.Unselected
                navigation.bringToFront(config)
            }
        }
    }

    fun onMemberSelect(userId: UUID) {
        selectedMember.value = userId
    }

    fun onCloseProfileClick() {
        selectedMember.value = null
    }

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
        componentScope.launch {
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
                StudentAction.Edit -> navigation.bringToFront(
                    UserEditorConfig(
                        userId = memberAction.value!!.second,
                        role = UserRole.STUDENT,
                        groupId = groupId
                    )
                )
            }
        }
    }

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
