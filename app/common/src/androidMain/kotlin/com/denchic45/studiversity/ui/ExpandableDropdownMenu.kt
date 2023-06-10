package com.denchic45.studiversity.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun ExpandableDropdownMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    itemsContent: @Composable ColumnScope.() -> Unit
) {
    IconButton(onClick = { onExpandedChange(true) }) {
        Icon(Icons.Filled.MoreVert, "menu expand button")
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onExpandedChange(false) },
    ) {
        itemsContent()
    }
}