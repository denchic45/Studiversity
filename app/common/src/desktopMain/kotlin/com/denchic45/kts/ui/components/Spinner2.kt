@file:OptIn(ExperimentalMaterial3Api::class)

package com.denchic45.kts.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.model.MenuAction
import com.denchic45.kts.ui.model.MenuItem
import com.denchic45.kts.ui.theme.toDrawablePath

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Spinner2(
    text:String,
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    placeholder: String? = null,
    itemsContent: @Composable ColumnScope.()->Unit
) {
//    var selectedItem by remember { mutableStateOf(items.find { it.action == activeAction }) }

    Column {
        OutlinedTextField(
            value = text,
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

        DropdownMenu(
            expanded = expanded,
//            modifier = Modifier.width(240.dp),
            onDismissRequest = { onExpandedChange(false) }
        ) {
           itemsContent()
        }
    }
}