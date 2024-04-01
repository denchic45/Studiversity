package com.denchic45.studiversity.ui.specialty

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.api.specialty.model.SpecialtyResponse

@Composable
fun SpecialtyListItem(
    item: SpecialtyResponse,
    onClick: () -> Unit = {},
    trailingContent: (@Composable () -> Unit)? = null,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = { Text(item.name) },
        leadingContent = {
            Icon(
//                painter = painterResource("ic_specialty".tod), todo вернуть
                Icons.Outlined.Engineering,
                contentDescription = "specialty icon",
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        trailingContent = trailingContent,
        modifier = modifier.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    )
}