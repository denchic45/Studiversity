package com.denchic45.kts.ui.studygroup.members

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.model.GroupMembers
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.component.HeaderItemUI
import com.denchic45.kts.ui.components.UserListItem
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.theme.toDrawablePath
import java.util.UUID


@Composable
fun SelectableStudyGroupMembersScreen(component: StudyGroupMembersComponent) {
    val selectedItemId by remember { mutableStateOf<UUID?>(null) }
    StudyGroupMemberScreen(component, selectedItemId)
}

@Composable
private fun StudyGroupMemberScreen(component: StudyGroupMembersComponent, selectedItemId: UUID?) {
    val members by component.members.collectAsState()
    StudyGroupMemberContent(members, component, selectedItemId)
}

@Composable
private fun StudyGroupMemberContent(
    members: Resource<GroupMembers>,
    component: StudyGroupMembersComponent,
    selectedItemId: UUID?,
) {
    members.let {
        val options by component.memberAction.collectAsState()
        it.onSuccess {
            MemberList(
                curator = it.curator,
                students = it.students,
                selectedItemId = selectedItemId,
                actions = options,
                onClick = component::onMemberSelect,
                onExpandActions = component::onExpandMemberAction,
                onClickAction = component::onClickMemberAction,
                onDismissAction = component::onDismissAction
            )
        }
    }
}

@Composable
private fun StudentListItem(
    userItem: UserItem,
    selected: Boolean,
    onClick: (id: UUID) -> Unit,
    onExpandActions: (memberId: UUID) -> Unit,
    onClickAction: (StudyGroupMembersComponent.StudentAction) -> Unit,
    onDismissAction: () -> Unit,
    actions: Pair<List<StudyGroupMembersComponent.StudentAction>, UUID>?,
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
        DropdownMenu(expanded = expanded && actions?.second == userItem.id,
            modifier = Modifier.width(240.dp),
            onDismissRequest = {
                expanded = false
                onDismissAction()
            }) {

            actions?.first?.forEach { action ->
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
    curator: UserItem?,
    students: List<UserItem>,
    selectedItemId: UUID?,
    onClick: (UUID) -> Unit,
    onExpandActions: (memberId: UUID) -> Unit,
    onClickAction: (StudyGroupMembersComponent.StudentAction) -> Unit,
    onDismissAction: () -> Unit,
    actions: Pair<List<StudyGroupMembersComponent.StudentAction>, UUID>?,
) {
    LazyColumn(
        modifier,
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
    ) {

        curator?.let {
            item { HeaderItemUI("Куратор") }

            item {
                UserListItem(
                    item = curator,
                    onClick = onClick,
                    actionsVisible = false,
                    selected = curator.id == selectedItemId
                )
            }
        }

        item { HeaderItemUI("Студенты") }

        items(students) {
            StudentListItem(
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