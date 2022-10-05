package com.denchic45.kts.ui.group.members

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.denchic45.kts.domain.usecase.FindGroupMembersUseCase
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.model.toUserItem
import com.denchic45.kts.ui.profile.ProfileComponent
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class GroupMembersComponent(
    findGroupMembersUseCase: FindGroupMembersUseCase, componentContext: ComponentContext,
    profileComponent: (userId: String) -> ProfileComponent,
    groupId: String,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val stack: Value<ChildStack<Config, Child>> = childStack(source = navigation,
        initialConfiguration = Config.Unselected,
        childFactory = { config: Config, componentContext: ComponentContext ->
            when (config) {
                Config.Unselected -> Child.Unselected
                is Config.MemberProfile -> Child.MemberProfile(profileComponent(config.userId))
            }
        })

    private val componentScope = componentScope()

    val groupMembers: StateFlow<Pair<UserItem, List<UserItem>>?> =
        findGroupMembersUseCase(groupId).map { members ->
            members.curator.toUserItem(members) to members.students.map { it.toUserItem(members) }
        }.stateIn(componentScope, SharingStarted.Lazily, null)

    val selectedMember = MutableStateFlow<String?>(null)

    init {
        componentScope.launch {
            selectedMember.collect { userId ->
                val config = if (userId != null) Config.MemberProfile(userId) else Config.Unselected
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

    sealed class Child {
        object Unselected : Child()
        class MemberProfile(val profileComponent: ProfileComponent) : Child()
    }

    sealed class Config : Parcelable {
        object Unselected : Config()
        class MemberProfile(val userId: String) : Config()
    }
}
