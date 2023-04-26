package com.denchic45.kts.ui.coursemembers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onLoading
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.CourseMembersComponent
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.stuiversity.api.membership.model.ScopeMember
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
    membersResource: Resource<List<ScopeMember>>,
    onMemberClick: (UUID) -> Unit
) {

    membersResource.onSuccess { members ->
        LazyColumn(contentPadding = PaddingValues(vertical = MaterialTheme.spacing.normal)) {
            items(members, key = { it.user.id }) {
                ListItem(
                    headlineContent = { Text(it.fullName) },
                    modifier = Modifier.clickable { onMemberClick(it.user.id) }
                )
            }
        }
    }.onLoading {
        Box(  modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
    }
}