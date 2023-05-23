@file:OptIn(ExperimentalMaterial3Api::class)

package com.denchic45.kts.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.denchic45.kts.ui.theme.toDrawablePath

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Spinner2(
    text: String,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    itemsContent: @Composable ColumnScope.() -> Unit,
) {

    Column {
        OutlinedTextField(
            value = text,
            onValueChange = {},
            modifier = modifier.clickable { onExpandedChange(!expanded) },
            placeholder = placeholder,
            label = label,
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
            onDismissRequest = { onExpandedChange(false) }
        ) {
            itemsContent()
        }
    }
}