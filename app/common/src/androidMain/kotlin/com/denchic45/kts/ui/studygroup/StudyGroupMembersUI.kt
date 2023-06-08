package com.denchic45.kts.ui.studygroup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.R
import com.denchic45.kts.domain.model.GroupMembers
import com.denchic45.kts.ui.ResourceContent
import com.denchic45.kts.ui.component.HeaderItemUI
import com.denchic45.kts.ui.search.IconTitleBox
import com.denchic45.kts.ui.search.UserListItem
import com.denchic45.kts.ui.studygroup.members.StudyGroupMembersComponent
import java.util.UUID

@Composable
fun StudyGroupMembersScreen(component: StudyGroupMembersComponent) {
    val members by component.members.collectAsState()
    ResourceContent(resource = members) {
        StudyGroupMemberContent(it, component::onMemberSelect)
    }
}

@Composable
fun StudyGroupMemberContent(
    members: GroupMembers,
    onMemberClick: (UUID) -> Unit,
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
    LazyColumn {
        members.curator?.let {
            item() { HeaderItemUI(name = "Куратор") }
            item(key = { members.curator.id }) {
                UserListItem(
                    item = members.curator,
                    modifier = Modifier.clickable { onMemberClick(it.id) }
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
                    modifier = Modifier.clickable { onMemberClick(it.id) }
                )
            }
        }
    }
}