@file:OptIn(ExperimentalMaterial3Api::class)

package com.denchic45.kts.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.model.MenuAction
import com.denchic45.kts.ui.model.MenuItem
import com.denchic45.kts.ui.theme.toDrawablePath

@Composable
fun <T : MenuAction> Spinner(
    items: List<MenuItem<T>>,
    onActionClick: (action: T) -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    placeholder: String? = null,
    activeAction: T?,
) {
    var selectedItem by remember { mutableStateOf(items.find { it.action == activeAction }) }

    Column {
        OutlinedTextField(
            value = selectedItem?.title ?: "",
            onValueChange = {},
            modifier.clickable { onExpandedChange(!expanded) },
            label = { placeholder?.let { Text(it) } },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                disabledLabelColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                disabledBorderColor = MaterialTheme.colorScheme.outline
            ),
            trailingIcon = {
                Icon(
                    painterResource("arrow_drop_down".toDrawablePath()),
                    null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            enabled = false,
            singleLine = true
        )

        DropdownMenu(expanded = expanded,
            modifier = Modifier.width(240.dp),
            onDismissRequest = { onExpandedChange(false) }
        ) {
            items.forEach { item ->
                DropdownMenuItem(onClick = {
                    onActionClick(item.action)
                    selectedItem = item
                }, enabled = item.enabled) {
                    item.action.iconName?.let {
                        Icon(
                            painter = painterResource(it.toDrawablePath()),
                            null,
                            Modifier.padding(end = 12.dp)
                        )
                    }
                    Text(text = item.title, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}