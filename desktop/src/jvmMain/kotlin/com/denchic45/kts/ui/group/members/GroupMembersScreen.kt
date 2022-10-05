package com.denchic45.kts.ui.group.members

import androidx.compose.foundation.layout.*
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
import com.denchic45.kts.ui.components.HeaderItem
import com.denchic45.kts.ui.components.UserListItem
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.profile.ProfileScreen

@Composable
fun GroupMembersScreen(groupMembersComponent: GroupMembersComponent) {
    Row {
        val curatorWithStudents by groupMembersComponent.groupMembers.collectAsState()
        curatorWithStudents?.let { MembersList(Modifier.weight(3f), it.first, it.second) }
//        Spacer(Modifier.fillMaxHeight().background(Color.Blue).width(422.dp))
        ProfileScreen(
            Modifier.fillMaxHeight().width(422.dp),
            TODO("Получать profile component через DI или GroupMembersComponent")
        )
    }
}

@Composable
private fun MemberListItem(userItem: UserItem) {
    UserListItem(item = userItem, actionsOnHover = true) {
        IconButton(onClick = {}) {
            Icon(painterResource("drawable/ic_more_vert.xml"), null)
        }
    }
}

@Composable
fun MembersList(modifier: Modifier = Modifier, curator: UserItem, students: List<UserItem>) {
    LazyColumn(modifier.padding(horizontal = 24.dp), contentPadding = PaddingValues(top = 8.dp)) {
        item { HeaderItem("Куратор") }

        item { MemberListItem(curator) }

        item { HeaderItem("Студенты") }

        items(students) { MemberListItem(it) }
    }
}