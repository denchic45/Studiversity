package com.denchic45.kts.ui.coursemembers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onLoading
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.CourseMembersComponent
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.search.UserListItem
import com.denchic45.kts.ui.theme.spacing
import java.util.UUID

@Composable
fun CourseMembersScreen(component: CourseMembersComponent) {
    val members by component.members.collectAsState()
    CourseMembersContent(
        membersResource = members,
        onMemberClick = component::onMemberClick
    )
}

@Composable
fun CourseMembersContent(
    membersResource: Resource<List<UserItem>>,
    onMemberClick: (UUID) -> Unit
) {
    membersResource.onSuccess { members ->
        LazyColumn(contentPadding = PaddingValues(vertical = MaterialTheme.spacing.normal)) {
            items(members, key = { it.id }) {
                UserListItem(
                    item = it,
                    modifier = Modifier.clickable { onMemberClick(it.id) })
            }
        }
    }.onLoading {
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}