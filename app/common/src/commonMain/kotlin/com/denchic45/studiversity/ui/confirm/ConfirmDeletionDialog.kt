package com.denchic45.studiversity.ui.confirm

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmDeletionDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDialog(
        title = title,
        text = text,
        icon = { Icon(Icons.Outlined.Delete, null) },
        confirmText = "Удалить",
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        modifier = Modifier.widthIn(max = 412.dp)
    )
}