package com.denchic45.studiversity.ui.studygroupmembers

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun InviteStudyGroupMembersDialog(component: InviteStudyGroupMembersComponent) {
    val state = remember { component.state }

    AlertDialog(
        onDismissRequest = component::onDismissRequest,
        title = { Text("Добавить новых участников") },
        text = {
            InviteStudyGroupMembersContent(
                state = state,
                onQueryChange = component::onQueryChange,
                onRoleSelect = component::onRoleChange,
                onMemberSelect = component::onMemberSelect,
                onMemberRemove = component::onMemberRemove
            )
        },
        confirmButton = { FilledTonalButton(onClick = component::onSave) { Text("Добавить") } },
        dismissButton = { TextButton(onClick = component::onDismissRequest) { Text("Отмена") } }
    )
}