package com.denchic45.studiversity.ui.subject

import androidx.compose.runtime.Composable
import com.denchic45.studiversity.ui.confirm.ConfirmDeletionDialog

@Composable
fun RemoveSubjectConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDeletionDialog(
        title = "Удалить предмет?",
        text = "Курсы по данному предмету останутся. Восстановить предмет будет невозможно.",
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}