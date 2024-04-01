package com.denchic45.studiversity.ui.user

import androidx.compose.runtime.Composable
import com.denchic45.studiversity.ui.confirm.ConfirmDeletionDialog

@Composable
fun RemoveUserConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDeletionDialog(
        title = "Удалить пользователя?",
        text = "Удалятся все данные, связанные с пользователем. " +
                "Восстановить пользователя будет невозможно.",
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}