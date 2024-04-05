package com.denchic45.studiversity.ui.studygroup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.ui.BlockContent
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.ScreenScaffold
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.component.UserListItem
import com.denchic45.studiversity.ui.main.CustomAppBar
import java.util.*


@Composable
fun StudyGroupMembersScreen(component: StudyGroupMembersComponent) {
    val allowEditMembers by component.allowEditMembers.collectAsState()
    val members by component.members.collectAsState()

    val childSlot by component.childSlot.subscribeAsState()
    childSlot.child?.let {
        when (val child = it.instance) {
            is StudyGroupMembersComponent.SlotChild.InviteMembers -> InviteStudyGroupMembersDialog(child.component)
            is StudyGroupMembersComponent.SlotChild.MemberEditor -> StudyGroupMemberEditorDialog(child.component)
        }
    }

    BlockContent {
        ResourceContent(members) {
            ScreenScaffold(topBar = {
                CustomAppBar(
                    title = { Text("Участники") },
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
                    onMemberRemoveClick = component::onRemoveMemberClick
                )
            }
        }
    }
}

@Composable
private fun StudentListItem(
    item: UserItem,
    allowEdit: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    com.denchic45.studiversity.ui.user.UserListItem(
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
        modifier = Modifier.clickable(interactionSource = interactionSource, onClick = onClick, indication = null)
    )
}

@Composable
fun MemberList(
    modifier: Modifier = Modifier,
    curators: List<UserItem>,
    students: List<UserItem>,
    otherMembers: List<UserItem>,
    onClick: (UUID) -> Unit,
    allowEditMembers: Boolean,
    onMemberEditClick: (UUID) -> Unit,
    onMemberRemoveClick: (UUID) -> Unit,
) {
    LazyColumn(
        modifier.widthIn(max = 960.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
    ) {
        if (curators.isNotEmpty()) {
            item { HeaderItem(if (curators.size == 1) "Куратор" else "Кураторы") }
            items(curators) {
                UserListItem(
                    item = it,
                    onClick = onClick,
                    actionsVisible = false
                )
            }
        }

        item { HeaderItem("Студенты") }
        items(students) {
            StudentListItem(
                item = it,
                allowEdit = allowEditMembers,
                onClick = { onClick(it.id) },
                onEditClick = { onMemberEditClick(it.id) },
                onRemoveClick = { onMemberRemoveClick(it.id) }
            )
        }

        if (otherMembers.isNotEmpty()) {
            item { HeaderItem("Другие участники") }
            items(students) {
                UserListItem(
                    item = it,
                    onClick = onClick,
                )
            }
        }
    }
}