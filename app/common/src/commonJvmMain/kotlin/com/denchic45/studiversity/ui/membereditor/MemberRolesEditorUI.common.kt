package com.denchic45.studiversity.ui.membereditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.scopedRoleName
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.user.UserListItem

@Composable
fun MemberRolesEditorContent(state: EditingMemberState, onRoleClick: (Role) -> Unit) {
    Column {
        UserListItem(state.member)
        HeaderItem("Роли")
        state.assignableRoles.forEach { role ->
            ListItem(
                headlineContent = { Text(role.scopedRoleName()) },
                trailingContent = { Checkbox(role in state.assignedRoles, null) },
                modifier = Modifier.clickable(onClick = { onRoleClick(role) })
            )
        }
    }
}