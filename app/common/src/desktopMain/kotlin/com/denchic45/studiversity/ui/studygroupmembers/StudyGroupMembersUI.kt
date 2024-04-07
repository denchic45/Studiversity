package com.denchic45.studiversity.ui.studygroupmembers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.ui.BlockContent
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.ScreenScaffold
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.confirm.ConfirmDeletionDialog
import com.denchic45.studiversity.ui.main.AppBarTitle
import com.denchic45.studiversity.ui.main.CustomAppBar
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.user.UserListItem
import java.util.UUID


@Composable
fun StudyGroupMembersScreen(component: StudyGroupMembersComponent) {
    val allowEditMembers by component.allowEditMembers.collectAsState()
    val members by component.members.collectAsState()

    val childSlot by component.childSlot.subscribeAsState()
    childSlot.child?.let {
        when (val child = it.instance) {
            is StudyGroupMembersComponent.SlotChild.InviteMembers -> {
                InviteStudyGroupMembersDialog(child.component)
            }

            is StudyGroupMembersComponent.SlotChild.MemberEditor -> {
                StudyGroupMemberEditorDialog(child.component)
            }
        }
    }

    var confirmDeletion by remember { mutableStateOf<UUID?>(null) }
    confirmDeletion?.let { userId ->
        ConfirmDeletionDialog(
            title = "Удалить участника группы?",
            text = "Данные, связанные с участников в группе, невозможно будет восстановить.",
            onConfirm = {
                component.onRemoveMemberClick(userId)
                confirmDeletion = null
            },
            onDismiss = {
                confirmDeletion = null
            }
        )
    }

    BlockContent {
        ResourceContent(members) {
            ScreenScaffold(topBar = {
                CustomAppBar(
                    title = { AppBarTitle("Участники") },
                    actions = {
                        if (allowEditMembers.takeValueIfSuccess() == true)
                            Button(onClick = component::onAddMemberClick) {
                                Icon(Icons.Outlined.Add, null)
                                Text("Добавить участника")
                            }
                    })
            }) {
                MemberList(
                    curators = it.curators,
                    students = it.students,
                    otherMembers = it.otherMembers,
                    onClick = component::onMemberClick,
                    allowEditMembers = allowEditMembers.takeValueIfSuccess() == true,
                    onMemberEditClick = component::onEditMemberClick,
                    onMemberRemoveClick = { confirmDeletion = it }
                )
            }
        }
    }
}

@Composable
private fun MemberListItem(
    item: UserItem,
    allowEdit: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    UserListItem(
        item = item,
        trailingContent = if (allowEdit) {
            {
                if (isHovered) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onRemoveClick) {
                            Icon(Icons.Outlined.PersonRemove, null)
                        }

                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Outlined.Edit, null)
                        }
                    }
                }
            }
        } else null,
        modifier = Modifier.clickable(
            onClick = onClick,
            interactionSource = interactionSource,
            indication = rememberRipple()
        )
    )
}

@Composable
fun MemberList(
    curators: List<UserItem>,
    students: List<UserItem>,
    otherMembers: List<UserItem>,
    onClick: (UUID) -> Unit,
    allowEditMembers: Boolean,
    onMemberEditClick: (UserItem) -> Unit,
    onMemberRemoveClick: (UUID) -> Unit,
) {
    LazyColumn(contentPadding = PaddingValues(bottom = MaterialTheme.spacing.normal)) {
        if (curators.isNotEmpty()) {
            item { HeaderItem(if (curators.size == 1) "Куратор" else "Кураторы") }
            items(curators) {
                MemberListItem(
                    item = it,
                    allowEdit = allowEditMembers,
                    onClick = { onClick(it.id) },
                    onEditClick = { onMemberEditClick(it) },
                    onRemoveClick = { onMemberRemoveClick(it.id) }
                )
            }
        }

        if (students.isNotEmpty()) {
            item { HeaderItem("Студенты") }
            items(students) {
                MemberListItem(
                    item = it,
                    allowEdit = allowEditMembers,
                    onClick = { onClick(it.id) },
                    onEditClick = { onMemberEditClick(it) },
                    onRemoveClick = { onMemberRemoveClick(it.id) }
                )
            }
        }

        if (otherMembers.isNotEmpty()) {
            item { HeaderItem("Другие участники") }
            items(otherMembers) {
                MemberListItem(
                    item = it,
                    allowEdit = allowEditMembers,
                    onClick = { onClick(it.id) },
                    onEditClick = { onMemberEditClick(it) },
                    onRemoveClick = { onMemberRemoveClick(it.id) }
                )
            }
        }
    }
}