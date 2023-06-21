package com.denchic45.studiversity.ui.search

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.common.R
import com.denchic45.stuiversity.api.room.model.RoomResponse

@Composable
fun RoomChooserScreen(component: RoomChooserComponent) {
    SearchScreen(
        component = component,
        keyItem = { it.id }, itemContent = {
            RoomListItem(it)
        })
}

@Composable
fun RoomListItem(item: RoomResponse, trailingContent: (@Composable () -> Unit)? = null) {
    ListItem(
        headlineContent = { Text(item.name) },
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.ic_room),
                contentDescription = "room icon",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        trailingContent = trailingContent,
    )
}