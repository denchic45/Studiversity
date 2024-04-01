package com.denchic45.studiversity.ui.confirm

import androidx.compose.runtime.Composable

@Composable
fun DismissChangesConfirmDialog(
    show: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDialog(
        show = show,
        title = "Не сохранять изменения?",
        confirmText = "Да",
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}