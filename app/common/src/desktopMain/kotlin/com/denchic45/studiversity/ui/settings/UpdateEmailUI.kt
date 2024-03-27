package com.denchic45.studiversity.ui.settings

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun UpdateEmailDialog(component: UpdateEmailComponent) {
    val state = remember { component.state }
    AlertDialog(onDismissRequest = component::onDismissRequest,
        title = { Text("Обновить почту") },
        text = {
            OutlinedTextField(
                value = state.email,
                onValueChange = component::onEmailChange,
                supportingText = { Text(state.emailMessage.orEmpty()) },
                isError = state.emailMessage != null
            )
        },
        confirmButton = { FilledTonalButton(onClick = component::onUpdateEmailClick) { Text("Отправить письмо") } },
        dismissButton = { TextButton(onClick = component::onDismissRequest) { Text("Отмена") } }
    )
}