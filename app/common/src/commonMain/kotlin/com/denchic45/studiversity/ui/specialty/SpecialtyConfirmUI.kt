package com.denchic45.studiversity.ui.specialty

import androidx.compose.runtime.Composable
import com.denchic45.studiversity.ui.confirm.ConfirmDeletionDialog

@Composable
fun RemoveSpecialtyConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDeletionDialog(
        title = "Удалить специальность?",
        text = "Группы с данной специальностью останутся доступны. " +
                "Восстановить специальность будет невозможно.",
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}