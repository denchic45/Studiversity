package com.denchic45.kts.ui.studygroup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.denchic45.kts.domain.model.GroupMembers
import com.denchic45.kts.ui.ResourceContent
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
    LazyColumn {
        members.curator?.let {
            item(key = { members.curator.id }) {
                UserListItem(
                    item = members.curator,
                    modifier = Modifier.clickable { onMemberClick(it.id) }
                )
            }
        }
        items(members.students, key = { it.id }) {
            UserListItem(
                item = it,
                modifier = Modifier.clickable { onMemberClick(it.id) }
            )
        }
    }
}