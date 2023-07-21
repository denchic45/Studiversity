package com.denchic45.studiversity.ui.studygroup

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
import com.denchic45.studiversity.common.R
import com.denchic45.studiversity.domain.model.GroupMembers
import com.denchic45.studiversity.domain.takeValueIfSuccess
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.ExpandableDropdownMenu
import com.denchic45.studiversity.ui.component.HeaderItemUI
import com.denchic45.studiversity.ui.component.IconTitleBox
import com.denchic45.studiversity.ui.search.UserListItem
import com.denchic45.studiversity.ui.studygroup.members.StudyGroupMembersComponent
import com.denchic45.studiversity.ui.theme.spacing
import java.util.UUID

@Composable
fun StudyGroupMembersScreen(component: StudyGroupMembersComponent) {
    val members by component.members.collectAsState()
    val allowEditMembers by component.allowEditMembers.collectAsState()

    ResourceContent(resource = members) {
        StudyGroupMembersContent(
            members = it,
            allowEditMembers = allowEditMembers.takeValueIfSuccess() ?: false,
            onMemberClick = component::onMemberSelect,
            onMemberEditClick = component::onMemberEditClick,
            onMemberRemoveClick = component::onMemberRemoveClick,
            onMemberSetHeadmanClick = component::onMemberSetHeadmanClick,
            onMemberRemoveHeadmanClick = component::onMemberRemoveHeadmanClick,
        )
    }
}

@Composable
fun StudyGroupMembersContent(
    members: GroupMembers,
    allowEditMembers: Boolean,
    onMemberClick: (UUID) -> Unit,
    onMemberEditClick: (UUID) -> Unit,
    onMemberRemoveClick: (UUID) -> Unit,
    onMemberSetHeadmanClick: (UUID) -> Unit,
    onMemberRemoveHeadmanClick: (UUID) -> Unit
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
            if (allowEditMembers) {
                var expanded by remember { mutableStateOf(false) }
                ExpandableDropdownMenu(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }) {
                    if (isStudent) {
                        if (members.headmanId == memberId) {
                            DropdownMenuItem(
                                text = { Text("Лишить прав старосты") },
                                onClick = {
                                    expanded = false
                                    onMemberRemoveHeadmanClick(memberId)
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Назначить старостой") },
                                onClick = {
                                    expanded = false
                                    onMemberSetHeadmanClick(memberId) }
                            )
                        }
                    }

                    DropdownMenuItem(
                        text = { Text("Изменить") },
                        onClick = {
                            expanded = false
                            onMemberEditClick(memberId)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Удалить") },
                        onClick = {
                            expanded = false
                            onMemberRemoveClick(memberId)
                        }
                    )
                }
            }
        }

    LazyColumn(contentPadding = PaddingValues(bottom = MaterialTheme.spacing.medium)) {
        members.curator?.let { userItem ->
            item { HeaderItemUI(name = "Куратор") }
            item(key = members.curator.id) {
                UserListItem(
                    item = members.curator,
                    modifier = Modifier.clickable { onMemberClick(userItem.id) },
                    trailingContent = {
                        memberTrailingContent(members.curator.id, false)
                    }
                )
            }
        }
        if (members.students.isNotEmpty()) {
            item {
                HeaderItemUI(name = "Студенты")
            }
            items(members.students, key = { it.id }) {
                UserListItem(
                    item = it,
                    modifier = Modifier.clickable { onMemberClick(it.id) },
                    trailingContent = {
                        memberTrailingContent(it.id, true)
                    }
                )
            }
        }
    }
}