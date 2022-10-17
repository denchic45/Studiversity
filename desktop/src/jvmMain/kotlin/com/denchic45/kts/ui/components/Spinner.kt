package com.denchic45.kts.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.model.MenuAction
import com.denchic45.kts.ui.model.MenuItem
import com.denchic45.kts.ui.theme.TextM2
import com.denchic45.kts.ui.theme.toDrawablePath

@Composable
fun <T : MenuAction> Spinner(
    items: List<MenuItem<T>>,
    onActionClick: (action: T) -> Unit,
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
            Modifier.clickable { onExpandedChange(!expanded) },
            label = { placeholder?.let { TextM2(it) } },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.medium)
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