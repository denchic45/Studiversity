package com.denchic45.studiversity.ui.studygroup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.component.UserListItem
import com.denchic45.studiversity.ui.studygroup.members.StudyGroupMembersComponent
import java.util.*


@Composable
fun StudyGroupMembersScreen(component: StudyGroupMembersComponent) {
    val allowEditMembers by component.allowEditMembers.collectAsState()
    val members by component.members.collectAsState()
    ResourceContent(members) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
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