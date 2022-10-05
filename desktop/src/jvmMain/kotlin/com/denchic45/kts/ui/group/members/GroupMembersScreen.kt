package com.denchic45.kts.ui.group.members

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.kts.ui.components.HeaderItem
import com.denchic45.kts.ui.components.UserListItem
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.profile.ProfileScreen

@Composable
fun GroupMembersScreen(groupMembersComponent: GroupMembersComponent) {
    Row {
        val curatorWithStudents by groupMembersComponent.groupMembers.collectAsState()
        val selectedItemId by groupMembersComponent.selectedMember.collectAsState()

        curatorWithStudents?.let {
            MemberList(modifier = Modifier.weight(3f),
                curator = it.first,
                students = it.second,
                selectedItemId = selectedItemId,
                onClick = { id -> groupMembersComponent.onMemberSelect(id) })
        }
        val stack by groupMembersComponent.stack.subscribeAsState()

        when (val child = stack.active.instance) {
            GroupMembersComponent.Child.Unselected -> {

            }
            is GroupMembersComponent.Child.MemberProfile -> {
                ProfileScreen(Modifier.fillMaxHeight().width(422.dp),
                    child.profileComponent) { groupMembersComponent.onCloseProfileClick() }
            }
        }
    }
}

@Composable
private fun MemberListItem(userItem: UserItem, selected: Boolean, onClick: (id: String) -> Unit) {
    UserListItem(item = userItem, onClick = onClick, actionsOnHover = true, selected = selected) {
        IconButton(onClick = {}) {
            Icon(painterResource("drawable/ic_more_vert.xml"), null)
        }
    }
}

@Composable
fun MemberList(
    modifier: Modifier = Modifier,
    curator: UserItem,
    students: List<UserItem>,
    selectedItemId: String?,
    onClick: (String) -> Unit,
) {
    LazyColumn(modifier,
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)) {
        item { HeaderItem("Куратор") }

        item { MemberListItem(curator, curator.id == selectedItemId, onClick) }

        item { HeaderItem("Студенты") }

        items(students) { MemberListItem(it, it.id == selectedItemId, onClick) }
    }
}