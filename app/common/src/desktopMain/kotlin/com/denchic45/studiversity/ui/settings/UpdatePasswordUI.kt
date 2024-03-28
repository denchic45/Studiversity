package com.denchic45.studiversity.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.ui.theme.spacing

@Composable
fun UpdatePasswordDialog(component: UpdatePasswordComponent) {
    val state = remember { component.state }
    AlertDialog(onDismissRequest = component::onDismissRequest,
        title = { Text("Изменить пароль") },
        text = {
            Column(Modifier.padding(horizontal = MaterialTheme.spacing.normal)) {
                OutlinedTextField(
                    value = state.oldPassword,
                    onValueChange = component::onOldPasswordChange,
                    placeholder = { Text("Старый пароль") },
                    supportingText = { Text(state.oldPasswordMessage.orEmpty()) },
                    isError = state.oldPasswordMessage != null,
                )

                OutlinedTextField(
                    value = state.newPassword,
                    onValueChange = component::onNewPasswordChange,
                    placeholder = { Text("Новый пароль") },
                    supportingText = { Text(state.newPasswordMessage.orEmpty()) },
                    isError = state.newPasswordMessage != null,
                )

                OutlinedTextField(
                    value = state.repeatedNewPassword,
                    onValueChange = component::onRepeatedNewPasswordChange,
                    placeholder = { Text("Повторите пароль") },
                    supportingText = { Text(state.repeatedNewPasswordMessage.orEmpty()) },
                    isError = state.repeatedNewPasswordMessage != null,
                )

                Text(state.resultMessage)
            }
        },
        confirmButton = { FilledTonalButton(onClick = component::onSave) { Text("Обновить пароль") } },
        dismissButton = { TextButton(onClick = component::onDismissRequest) { Text("Отмена") } }
    )
}