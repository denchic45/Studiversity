package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.api.room.model.RoomResponse
import com.denchic45.studiversity.ui.room.RemoveRoomConfirmDialog
import com.denchic45.studiversity.ui.room.RoomListItem
import com.denchic45.studiversity.ui.roomeditor.RoomEditorDialog
import java.util.*

@Composable
fun RoomsAdminScreen(component: RoomsAdminComponent) {
    val childSlot by component.childSlot.subscribeAsState()

    childSlot.child?.let {
        RoomEditorDialog(it.instance.component)
    }

    var confirmDeletion by remember { mutableStateOf<UUID?>(null) }
    confirmDeletion?.let { userId ->
        RemoveRoomConfirmDialog(
            onConfirm = {
                component.onRemoveClick(userId)
                confirmDeletion = null
            },
            onDismiss = { confirmDeletion = null }
        )
    }

    AdminSearchScreen(
        component = component,
        keyItem = RoomResponse::id,
        searchPlaceholder = "Найти аудиторию",
        fabText = "Создать аудиторию"
    ) { item ->
        val interactionSource = remember { MutableInteractionSource() }
        RoomListItem(
            item, trailingContent = {
                val isHovered by interactionSource.collectIsHoveredAsState()
                if (isHovered)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { confirmDeletion = item.id }) {
                            Icon(Icons.Outlined.Delete, null)
                        }
                        IconButton(onClick = { component.onEditClick(item.id) }) {
                            Icon(Icons.Outlined.Edit, null)
                        }
                    }
            }, modifier = Modifier.hoverable(interactionSource)
        )
    }
}