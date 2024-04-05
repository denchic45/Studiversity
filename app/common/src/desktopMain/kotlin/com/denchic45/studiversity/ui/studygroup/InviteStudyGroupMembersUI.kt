package com.denchic45.studiversity.ui.studygroup

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.denchic45.studiversity.ui.component.AlertDialogTitle

@Composable
fun InviteStudyGroupMembersDialog(component: InviteStudyGroupMembersComponent) {
    val state = remember { component.state }

    AlertDialog(
        onDismissRequest = component::onDismissRequest,
        title = { AlertDialogTitle("Добавить новых участников", onClose = component::onDismissRequest) },
        text = {
            InviteStudyGroupMembersContent(
                state = state,
                onQueryChange = component::onQueryChange,
                onRoleSelect = component::onRoleChange,
                onMemberSelect = component::onMemberSelect,
                onMemberRemove = component::onMemberRemove
            )
        },
        confirmButton = { FilledTonalButton(onClick = component::onSave) { Text("Сохранить") } }
    )
}