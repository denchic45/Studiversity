package com.denchic45.kts.ui.studygroup.members

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.model.GroupMembers
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.component.HeaderItemUI
import com.denchic45.kts.ui.components.UserListItem
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.profile.ProfileSideBar
import com.denchic45.kts.ui.theme.toDrawablePath
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableStudyGroupMembersScreen(component: SelectableStudyGroupMembersComponent) {
    Row {
        val selectedItemId by component.selectedMember.collectAsState()
        Box(modifier = Modifier.weight(3f)) {
            StudyGroupMemberScreen(component.studyGroupMembersComponent, selectedItemId)
        }

        val overlay by component.childOverlay.subscribeAsState()

        Box(Modifier.width(472.dp)) {
            when (val child = overlay.overlay?.instance) {
                is SelectableStudyGroupMembersComponent.OverlayChild.Member -> ProfileSideBar(
                    Modifier,
                    child.component, 
                    component::onCloseProfileClick)
                null -> {}
            }
        }
    }
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