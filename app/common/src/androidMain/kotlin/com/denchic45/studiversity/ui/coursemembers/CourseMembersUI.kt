package com.denchic45.studiversity.ui.coursemembers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.onLoading
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.takeValueIfSuccess
import com.denchic45.studiversity.ui.CourseMembersComponent
import com.denchic45.studiversity.ui.ExpandableDropdownMenu
import com.denchic45.studiversity.ui.model.UserItem
import com.denchic45.studiversity.ui.search.UserListItem
import com.denchic45.studiversity.ui.theme.spacing
import java.util.UUID

@Composable
fun CourseMembersScreen(component: CourseMembersComponent) {
    val members by component.members.collectAsState()
    val allowEditMembers by component.allowEdit.collectAsState()

    CourseMembersContent(
        membersResource = members,
        allowEditMembers = allowEditMembers.takeValueIfSuccess() ?: false,
        onMemberClick = component::onMemberClick,
        onMemberEditClick = component::onMemberEditClick,
        onMemberRemoveClick = component::onMemberRemoveClick
    )
}

@Composable
fun CourseMembersContent(
    membersResource: Resource<List<UserItem>>,
    allowEditMembers: Boolean,
    onMemberClick: (UUID) -> Unit,
    onMemberEditClick: (UUID) -> Unit,
    onMemberRemoveClick: (UUID) -> Unit
) {

    val memberTrailingContent: @Composable (UUID) -> Unit = { memberId ->
        if (allowEditMembers) {
            var expanded by remember { mutableStateOf(false) }
            ExpandableDropdownMenu(
                expanded = expanded,
                onExpandedChange = { expanded = it }) {

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
        } else Box(Modifier.size(40.dp))
    }

    membersResource.onSuccess { members ->
        LazyColumn(contentPadding = PaddingValues(vertical = MaterialTheme.spacing.normal)) {
            items(members, key = { it.id }) {
                UserListItem(
                    item = it,
                    modifier = Modifier.clickable { onMemberClick(it.id) },
                    trailingContent = { memberTrailingContent(it.id) }
                )
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