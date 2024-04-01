package com.denchic45.studiversity.ui.studygroup

import androidx.compose.runtime.Composable
import com.denchic45.studiversity.ui.confirm.ConfirmDeletionDialog

@Composable
fun RemoveStudyGroupConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDeletionDialog(
        title = "Удалить группу?",
        text = "Удалятся все данные, связанные с группой. " +
                "Восстановить группу будет невозможно.",
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}