package com.denchic45.studiversity.ui.studygroupmembers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.common.R
import com.denchic45.studiversity.domain.model.StudyGroupMembers
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.ExpandableDropdownMenu
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.component.IconTitleBox
import com.denchic45.studiversity.ui.confirm.ConfirmDeletionDialog
import com.denchic45.studiversity.ui.search.UserListItem
import com.denchic45.studiversity.ui.theme.spacing
import java.util.*

@Composable
fun StudyGroupMembersScreen(component: StudyGroupMembersComponent) {
    val membersResource by component.members.collectAsState()
    val allowEditMembers by component.allowEditMembers.collectAsState()

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

    ResourceContent(resource = membersResource) { members ->
        StudyGroupMembersContent(
            members = members,
            allowEditMembers = allowEditMembers.takeValueIfSuccess() ?: false,
            onMemberClick = component::onMemberClick,
            onMemberEditClick = component::onEditMemberClick,
            onMemberRemoveClick = { confirmDeletion = it }
        )
    }
}

@Composable
fun StudyGroupMembersContent(
    members: StudyGroupMembers,
    allowEditMembers: Boolean,
    onMemberClick: (UUID) -> Unit,
    onMemberEditClick: (UserItem) -> Unit,
    onMemberRemoveClick: (UUID) -> Unit
) {
    if (members.isEmpty())
        IconTitleBox(
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_study_group),
                    "empty members",
                    tint = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(78.dp)
                )
            },
            title = { Text(text = "Здесь пока еще нет участников") }
        )

    val memberTrailingContent: @Composable (UUID, isStudent: Boolean) -> Unit =
        { memberId, isStudent ->

        }

    LazyColumn(contentPadding = PaddingValues(bottom = MaterialTheme.spacing.medium)) {
        members.curators.takeIf(List<UserItem>::isNotEmpty)?.let { curators ->
            item { HeaderItem(if (curators.size == 1) "Куратор" else "Кураторы") }
            items(curators) {
                UserListItem(
                    item = it,
                    modifier = Modifier.clickable(onClick = { onMemberClick(it.id) })
                )
            }
        }
        if (members.students.isNotEmpty()) {
            item {
                HeaderItem(name = "Студенты")
            }
            items(members.students, key = { it.id }) {
                UserListItem(
                    item = it,
                    modifier = Modifier.clickable { onMemberClick(it.id) },
                    trailingContent = {
                        if (allowEditMembers) {
                            var expanded by remember { mutableStateOf(false) }
                            ExpandableDropdownMenu(
                                expanded = expanded,
                                onExpandedChange = { expanded = it }) {
                                DropdownMenuItem(
                                    text = { Text("Изменить") },
                                    onClick = {
                                        expanded = false
                                        onMemberEditClick(it)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Удалить") },
                                    onClick = {
                                        expanded = false
                                        onMemberRemoveClick(it.id)
                                    }
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}