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
    findGroupMembersUseCase: FindGroupMembersUseCase,
    componentContext: ComponentContext,
    private val setHeadmanUseCase: SetHeadmanUseCase,
    private val removeHeadmanUseCase: RemoveHeadmanUseCase,
    profileComponent: (navigator: StackNavigator<in GroupConfig.Group>, groupClickable: Boolean, userId: String) -> ProfileComponent,
    userEditorComponent: (onFinish: () -> Unit, config: UserEditorConfig) -> UserEditorComponent,
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
                        userEditorComponent(navigation::pop, config)
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

    val studentAction: MutableStateFlow<Pair<List<StudentAction>, String>> =
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
        studentAction.update {
            listOf(
                if (groupMembers.value?.headmanId == memberId) StudentAction.RemoveHeadman
                else StudentAction.SetHeadman,
                StudentAction.Edit
            ) to memberId
        }
    }

    fun onClickMemberAction(action: StudentAction) {
        componentScope.launch {
            when (action) {
                StudentAction.SetHeadman -> setHeadmanUseCase(studentAction.value.second, groupId)
                StudentAction.RemoveHeadman -> removeHeadmanUseCase(groupId)
                StudentAction.Edit -> {
                    navigation.bringToFront(
                        UserEditorConfig(
                            userId = studentAction.value.second,
                            role = UserRole.STUDENT,
                            groupId = groupId
                        )
                    )
                }
            }
        }
    }

    fun onDismissAction() {
        studentAction.value = Pair(listOf(), "")
    }


    enum class StudentAction(
        override val title: String,
        override val iconName: String? = null,
    ) :
        MenuAction {
        SetHeadman("Назначить старостой"),
        RemoveHeadman("Лишить прав старосты"),
        Edit("Редактировать")
    }
}
