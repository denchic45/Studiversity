package com.denchic45.kts.ui.group.members

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.model.GroupMember
import com.denchic45.kts.domain.usecase.FindGroupMembersUseCase
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.model.toUserItem
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject

@Inject
class GroupMembersComponent(
    findGroupMembersUseCase: FindGroupMembersUseCase, componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val groupMembers: StateFlow<Pair<UserItem, List<UserItem>>?> =
        findGroupMembersUseCase("groupId").map { members ->
            members.curator.toUserItem(members) to members.students.map { it.toUserItem(members) }
        }.stateIn(componentScope, SharingStarted.Lazily, null)

    val selectedMember = MutableStateFlow<GroupMember?>(null)
}
