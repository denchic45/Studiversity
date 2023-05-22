package com.denchic45.kts.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableMenu(itemsContent: @Composable ColumnScope.() -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }
    IconButton(onClick = { menuExpanded = !menuExpanded }) {
        Icon(Icons.Filled.MoreVert, "menu expand button")
    }
    DropdownMenu(
        expanded = menuExpanded,
        offset = DpOffset(x = (-84).dp, y = 0.dp),
        onDismissRequest = { menuExpanded = false },
    ) {
        itemsContent()
    }
}