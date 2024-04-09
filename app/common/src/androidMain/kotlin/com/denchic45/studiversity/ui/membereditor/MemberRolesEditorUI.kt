package com.denchic45.studiversity.ui.membereditor

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.domain.resource.onSuccess

@Composable
fun MemberRolesEditorDialog(component: MemberRolesEditorComponent) {
    val stateResource by component.stateResource.collectAsState()

    stateResource.onSuccess { state ->
        AlertDialog(
            onDismissRequest = component::onDismissRequest,
            title = { Text("Обновить роли участника") },
            text = { MemberRolesEditorContent(state, component::onRoleClick) },
            confirmButton = {
                FilledTonalButton(
                    component::onSave,
                    enabled = state.allowSave
                ) { Text("Сохранить") }
            },
            dismissButton = { TextButton(component::onDismissRequest) { Text("Отменить") } }
        )
    }
}