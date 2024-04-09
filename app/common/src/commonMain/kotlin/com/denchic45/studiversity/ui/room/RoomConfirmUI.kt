package com.denchic45.studiversity.ui.room

import androidx.compose.runtime.Composable
import com.denchic45.studiversity.ui.confirm.ConfirmDeletionDialog

@Composable
fun RemoveRoomConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDeletionDialog(
        title = "Удалить аудиторию?",
        text = "Аудитория будет очищена с учебных занятий, где она была указана. " +
                "Восстановить аудиторию будет невозможно.",
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}