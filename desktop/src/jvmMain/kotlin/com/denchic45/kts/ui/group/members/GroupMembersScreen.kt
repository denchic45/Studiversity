package com.denchic45.kts.ui.group.members

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.kts.ui.components.HeaderItem
import com.denchic45.kts.ui.components.UserListItem
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.navigation.GroupMembersChild
import com.denchic45.kts.ui.navigation.ProfileChild
import com.denchic45.kts.ui.profile.ProfileScreen
import com.denchic45.kts.ui.theme.toDrawablePath

@Composable
fun GroupMembersScreen(groupMembersComponent: GroupMembersComponent) {
    Row {
        val curatorWithStudents by groupMembersComponent.memberItems.collectAsState()
        val selectedItemId by groupMembersComponent.selectedMember.collectAsState()

        curatorWithStudents?.let {
            val options by groupMembersComponent.memberAction.collectAsState()
            MemberList(
                modifier = Modifier.weight(3f),
                curator = it.first,
                students = it.second,
                selectedItemId = selectedItemId,
                actions = options,
                onClick = groupMembersComponent::onMemberSelect,
                onExpandActions = groupMembersComponent::onExpandMemberAction,
                onClickAction = groupMembersComponent::onClickMemberAction,
                onDismissAction = groupMembersComponent::onDismissAction
            )
        }
        val stack by groupMembersComponent.stack.subscribeAsState()

        when (val child = stack.active.instance) {
            GroupMembersChild.Unselected -> {

            }
            is ProfileChild -> {
                ProfileScreen(
                    Modifier.fillMaxHeight().width(422.dp), child.profileComponent
                ) { groupMembersComponent.onCloseProfileClick() }
            }
        }
    }
}

@Composable
private fun MemberListItem(
    userItem: UserItem,
    selected: Boolean,
    onClick: (id: String) -> Unit,
    onExpandActions: (memberId: String) -> Unit,
    onClickAction: (GroupMembersComponent.MemberAction) -> Unit,
    onDismissAction: () -> Unit,
    actions: Pair<List<GroupMembersComponent.MemberAction>, String>,
) {
    val interactionSource = remember(::MutableInteractionSource)

    val hovered by interactionSource.collectIsHoveredAsState()

    var expanded by remember { mutableStateOf(false) }

    UserListItem(
        item = userItem,
        onClick = onClick,
        actionsVisible = expanded || hovered,
        selected = selected,
        interactionSource = interactionSource
    ) {
        IconButton({
            onExpandActions(userItem.id)
            expanded = true
        }) {
            Icon(painterResource("ic_more_vert".toDrawablePath()), null)
        }
        DropdownMenu(expanded = expanded && actions.second == userItem.id,
            modifier = Modifier.width(240.dp),
            onDismissRequest = {
                onDismissAction()
            }) {

            actions.first.forEach { action ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onClickAction(action)
                }) {
                    Text(text = action.title, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun MemberList(
    modifier: Modifier = Modifier,
    curator: UserItem,
    students: List<UserItem>,
    selectedItemId: String?,
    onClick: (String) -> Unit,
    onExpandActions: (memberId: String) -> Unit,
    onClickAction: (GroupMembersComponent.MemberAction) -> Unit,
    onDismissAction: () -> Unit,
    actions: Pair<List<GroupMembersComponent.MemberAction>, String>,
) {
    LazyColumn(
        modifier,
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
    ) {
        item { HeaderItem("Куратор") }

        item {
            MemberListItem(
                userItem = curator,
                selected = curator.id == selectedItemId,
                onClick = onClick,
                onExpandActions = onExpandActions,
                onClickAction = onClickAction,
                onDismissAction = onDismissAction,
                actions = actions
            )
        }

        item { HeaderItem("Студенты") }


        items(students) {
            MemberListItem(
                userItem = it,
                selected = it.id == selectedItemId,
                onClick = onClick,
                onExpandActions = onExpandActions,
                onClickAction = onClickAction,
                onDismissAction = onDismissAction,
                actions = actions
            )
        }
    }
}