package com.denchic45.studiversity.ui.studygroup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.scopedRoleName
import com.denchic45.studiversity.systemRoleName
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.theme.spacing

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InviteStudyGroupMembersContent(
    state: InvitedMembersState,
    onQueryChange: (String) -> Unit,
    onRoleSelect: (Role) -> Unit,
    onMemberSelect: (UserItem) -> Unit,
    onMemberRemove: (UserItem) -> Unit,
) {
    Column {
        HeaderItem("Выбранные пользователи")
        var membersExpanded by remember { mutableStateOf(false) }
        val showList = membersExpanded && state.foundMembers.isNotEmpty()
        ExposedDropdownMenuBox(
            expanded = showList,
            onExpandedChange = { membersExpanded = !membersExpanded },
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.normal)
        ) {
            OutlinedTextField(
                value = state.queryText,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                placeholder = { Text("Поиск пользователей") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showList) },
                singleLine = true,
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = showList,
                onDismissRequest = { membersExpanded = false }) {
                state.foundMembers.forEach {
                    DropdownMenuItem(
                        text = { Text(it.title) },
                        onClick = {
                            onMemberSelect(it)
                            membersExpanded = false
                        }
                    )
                }
            }
        }
        FlowRow {
            state.members.forEach {
                UserChip(it) { onMemberRemove(it) }
            }
        }

        var rolesExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = rolesExpanded,
            onExpandedChange = { rolesExpanded = it },
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.normal)
        ) {
            OutlinedTextField(
                value = state.role?.scopedRoleName().orEmpty(),
                onValueChange = { },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                readOnly = true,
                label = { Text("Роль") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(rolesExpanded) },
                singleLine = true,
//                isError = state.roleMessage != null,
//                supportingText = { Text(state.roleMessage.orEmpty()) }
            )

            ExposedDropdownMenu(
                expanded = rolesExpanded,
                onDismissRequest = { rolesExpanded = false }) {
                state.assignableRoles.forEach {
                    DropdownMenuItem(
                        text = { Text(it.systemRoleName()) },
                        onClick = {
                            rolesExpanded = false
                            onRoleSelect(it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserChip(item: UserItem, onRemove: () -> Unit) {
    AssistChip(
        onClick = { },
        label = { Text(text = item.title) },
        leadingIcon = {
            AsyncImage(
                model = item.avatarUrl,
                contentDescription = "user avatar",
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "remove member",
                modifier = Modifier.clickable(onClick = onRemove)
            )
        })
}