package com.denchic45.studiversity.ui.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.model.UserItem

@Composable
fun UserListItem(
    item: UserItem,
    onClick: () -> Unit = {},
    avatarContent: @Composable () -> Unit,
    trailingContent: (@Composable () -> Unit)? = null,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    modifier: Modifier = Modifier
) {
    item.apply {
        ListItem(
            headlineContent = {
                Column {
                    Text(title, style = MaterialTheme.typography.bodyLarge)
                    subtitle?.let { Text(it, style = MaterialTheme.typography.labelMedium) }
                }
            },
            leadingContent = { avatarContent() },
            trailingContent = trailingContent,
            modifier = modifier.height(64.dp)
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
        )
    }
}