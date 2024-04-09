package com.denchic45.studiversity.ui.room

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Room
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.api.room.model.RoomResponse

@Composable
fun RoomListItem(
    item: RoomResponse,
    trailingContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = { Text(item.name) },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.Room,
                contentDescription = "room",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        trailingContent = trailingContent,
        modifier = modifier
    )
}