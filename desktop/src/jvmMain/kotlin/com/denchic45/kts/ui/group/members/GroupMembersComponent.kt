package com.denchic45.kts.ui.group.members

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.domain.model.GroupMembers
import com.denchic45.kts.domain.usecase.FindGroupMembersUseCase
import com.denchic45.kts.domain.usecase.RemoveHeadmanUseCase
import com.denchic45.kts.domain.usecase.SetHeadmanUseCase
import com.denchic45.kts.ui.model.MenuAction
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.model.toUserItem
import com.denchic45.kts.ui.navigation.*
import com.denchic45.kts.ui.profile.ProfileComponent
import com.denchic45.kts.ui.usereditor.UserEditorComponent
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class GroupMembersComponent(
    findGroupMembersUseCase: FindGroupMembersUseCase, componentContext: ComponentContext,
    private val setHeadmanUseCase: SetHeadmanUseCase,
    private val removeHeadmanUseCase: RemoveHeadmanUseCase,
    profileComponent: (navigator: StackNavigator<in GroupConfig.Group>, groupClickable: Boolean, userId: String) -> ProfileComponent,
    userEditorComponent: (userId: String?, role: UserRole?, groupId: String?) -> UserEditorComponent,
    navigator: StackNavigator<in GroupConfig>,
    private val groupId: String,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<GroupMembersConfig>()

    val stack: Value<ChildStack<GroupMembersConfig, GroupMembersChild>> =
        childStack(source = navigation,
            initialConfiguration = GroupMembersConfig.Unselected,
            childFactory = { config, _ ->
                when (config) {
                    GroupMembersConfig.Unselected -> GroupMembersChild.Unselected
                    is ProfileConfig -> ProfileChild(
                        profileComponent(navigator, false, config.userId)
                    )
                    is UserEditorConfig -> UserEditorChild(
                        userEditorComponent(
                            config.userId,
                            config.role,
                            config.groupId
                        )
                    )
                }
            })

    private val componentScope = componentScope()

    private val groupMembers: StateFlow<GroupMembers?> =
        findGroupMembersUseCase(groupId).stateIn(componentScope, SharingStarted.Lazily, null)

    val memberItems: StateFlow<Pair<UserItem, List<UserItem>>?> =
        groupMembers.filterNotNull().map { members ->
            members.curator.toUserItem(members) to members.students.map { it.toUserItem(members) }
        }.stateIn(componentScope, SharingStarted.Lazily, null)

    val selectedMember = MutableStateFlow<String?>(null)

    val memberAction: MutableStateFlow<Pair<List<MemberAction>, String>> =
        MutableStateFlow(Pair(emptyList(), ""))

    init {
        componentScope.launch {
            selectedMember.collect { userId ->
                val config = if (userId != null) ProfileConfig(userId)
                else GroupMembersConfig.Unselected
                navigation.bringToFront(config)
            }
        }
    }

    fun onMemberSelect(userId: String) {
        selectedMember.value = userId
    }

    fun onCloseProfileClick() {
        selectedMember.value = null
    }

    fun onExpandMemberAction(memberId: String) {
        memberAction.update {
            listOf(
                if (groupMembers.value?.headmanId == memberId) MemberAction.RemoveHeadman
                else MemberAction.SetHeadman,
                MemberAction.Edit,
                MemberAction.Remove
            ) to memberId
        }
    }

    fun onClickMemberAction(action: MemberAction) {
        componentScope.launch {
            when (action) {
                MemberAction.SetHeadman -> setHeadmanUseCase(memberAction.value.second, groupId)
                MemberAction.RemoveHeadman -> removeHeadmanUseCase(groupId)
                MemberAction.Edit -> {
                    navigation.push(
                        UserEditorConfig(userId = memberAction.value.second, null, groupId)
                    )
                }
                MemberAction.Remove -> TODO()
            }
        }
    }

    fun onDismissAction() {
        memberAction.value = Pair(listOf(), "")
    }


    enum class MemberAction(
        override val title: String,
        override val iconName: String? = null,
    ) :
        MenuAction {
        SetHeadman("Назначить старостой"),
        RemoveHeadman("Лишить прав старосты"),
        Edit("Редактировать"),
        Remove("Удалить")
    }
}
