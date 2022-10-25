package com.denchic45.kts.ui.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties

@Preview
@Composable
fun Preview() {
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = {},
        modifier = Modifier,
        dismissButton = {},
        title = { Text(text = "Title dialog") },
        text = { Text(text = "Some content dialog") },
        properties = DialogProperties()
    )
}