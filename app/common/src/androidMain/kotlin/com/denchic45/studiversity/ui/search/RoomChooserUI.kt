package com.denchic45.studiversity.ui.search

import androidx.compose.runtime.Composable
import com.denchic45.studiversity.ui.room.RoomListItem

@Composable
fun RoomChooserScreen(component: RoomChooserComponent) {
    SearchScreen(
        component = component,
        keyItem = { it.id }, itemContent = {
            RoomListItem(it)
        })
}