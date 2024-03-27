package com.denchic45.studiversity.ui.settings

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun UpdatePasswordDialog(component: UpdatePasswordComponent) {
    val state = remember { component.state }
    AlertDialog(onDismissRequest = component::onDismissRequest,
        title = { Text("Изменить пароль") },
        text = {
          TODO()
        },
        confirmButton = { FilledTonalButton(onClick = component::onSave) { Text("Обновить пароль") } },
        dismissButton = { TextButton(onClick = component::onDismissRequest) { Text("Отмена") } }
    )
}