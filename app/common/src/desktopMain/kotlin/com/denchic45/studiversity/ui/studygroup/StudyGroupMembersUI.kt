package com.denchic45.studiversity.ui.studygroup

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.model.GroupMembers
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.component.UserListItem
import com.denchic45.studiversity.ui.studygroup.members.StudyGroupMembersComponent
import com.denchic45.studiversity.ui.theme.toDrawablePath
import java.util.*


@Composable
fun SelectableStudyGroupMembersScreen(
    component: StudyGroupMembersComponent,
    selectedItemId: UUID?,
) {
    StudyGroupMemberScreen(component, selectedItemId, component::onMemberClick)
}

@Composable
private fun StudyGroupMemberScreen(
    component: StudyGroupMembersComponent,
    selectedItemId: UUID?,
    onMemberSelect: (UUID) -> Unit,
) {
    val members by component.members.collectAsState()
    StudyGroupMemberContent(members, onMemberSelect, selectedItemId, component)
}

@Composable
private fun StudyGroupMemberContent(
    members: Resource<GroupMembers>,
    onClick: (UUID) -> Unit,
    selectedItemId: UUID?,
    component: StudyGroupMembersComponent,
) {
    ResourceContent(members) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            val actions by component.memberActions.collectAsState()
            MemberList(
                curator = it.curator,
                students = it.students,
                selectedItemId = selectedItemId,
                actions = actions,
                onClick = onClick,
                onExpandActions = component::onMemberActionsExpand,
                onMemberEditClick = component::onEditMemberClick,
                onMemberRemoveClick = component::onRemoveMemberClick,
                onMemberSetHeadmanClick = component::onMemberSetHeadmanClick,
                onMemberRemoveHeadmanClick = component::onRemoveHeadmanClick,
                onDismissActions = component::onDismissActions
            )
        }
    }
}

@Composable
private fun StudentListItem(
    userItem: UserItem,
    selected: Boolean,
    onClick: (UUID) -> Unit,
    onExpandActions: (memberId: UUID) -> Unit,
    actions: Pair<List<StudyGroupMembersComponent.MemberAction>, UUID>?,
    onMemberEditClick: (UUID) -> Unit,
    onMemberRemoveClick: (UUID) -> Unit,
    onMemberSetHeadmanClick: (UUID) -> Unit,
    onMemberRemoveHeadmanClick: (UUID) -> Unit,
    onDismissActions: () -> Unit,
) {
    val interactionSource = remember(::MutableInteractionSource)
    val isHovered by interactionSource.collectIsHoveredAsState()
    var expanded by remember { mutableStateOf(false) }

    UserListItem(
        item = userItem,
        onClick = onClick,
        actionsVisible = expanded || isHovered,
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
                onDismissActions()
            }) {

            actions?.first?.forEach { action ->
                when (action) {
                    StudyGroupMembersComponent.MemberAction.EDIT -> {
                        DropdownMenuItem(
                            text = { Text("Изменить") },
                            onClick = { onMemberEditClick(userItem.id) }
                        )
                    }

                    StudyGroupMembersComponent.MemberAction.REMOVE -> {
                        DropdownMenuItem(
                            text = { Text("Удалить") },
                            onClick = { onMemberRemoveClick(userItem.id) }
                        )
                    }

                    StudyGroupMembersComponent.MemberAction.SET_HEADMAN -> {
                        DropdownMenuItem(
                            text = { Text("Назначить старостой") },
                            onClick = { onMemberSetHeadmanClick(userItem.id) }
                        )
                    }

                    StudyGroupMembersComponent.MemberAction.REMOVE_HEADMAN -> {
                        DropdownMenuItem(
                            text = { Text("Лишить прав старосты") },
                            onClick = { onMemberRemoveHeadmanClick(userItem.id) }
                        )
                    }
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
    actions: Pair<List<StudyGroupMembersComponent.MemberAction>, UUID>?,
    onMemberEditClick: (UUID) -> Unit,
    onMemberRemoveClick: (UUID) -> Unit,
    onMemberSetHeadmanClick: (UUID) -> Unit,
    onMemberRemoveHeadmanClick: (UUID) -> Unit,
    onDismissActions: () -> Unit,
) {
    LazyColumn(
        modifier.widthIn(max = 960.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
    ) {

        curator?.let {
            item { HeaderItem("Куратор") }

            item {
                UserListItem(
                    item = curator,
                    onClick = onClick,
                    actionsVisible = false,
                    selected = curator.id == selectedItemId
                )
            }
        }

        item { HeaderItem("Студенты") }

        items(students) {
            StudentListItem(
                userItem = it,
                selected = it.id == selectedItemId,
                onClick = onClick,
                onExpandActions = onExpandActions,
                actions = actions,
                onMemberEditClick = onMemberEditClick,
                onMemberRemoveClick = onMemberRemoveClick,
                onMemberSetHeadmanClick = onMemberSetHeadmanClick,
                onMemberRemoveHeadmanClick = onMemberRemoveHeadmanClick,
                onDismissActions = onDismissActions
            )
        }
    }
}