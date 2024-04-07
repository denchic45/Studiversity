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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.ui.user.RemoveUserConfirmDialog
import com.denchic45.studiversity.ui.user.UserListItem
import com.denchic45.studiversity.ui.usereditor.UserEditorDialog
import java.util.UUID

@Composable
fun UsersAdminScreen(component: UsersAdminComponent) {
    val childSlot by component.childSlot.subscribeAsState()

    childSlot.child?.let {
        when (val child = it.instance) {
            is UsersAdminComponent.Child.UserEditor -> UserEditorDialog(child.component)
        }
    }

    var confirmDeletion by remember { mutableStateOf<UUID?>(null) }
    confirmDeletion?.let { userId ->
        RemoveUserConfirmDialog(
            onConfirm = {
                component.onRemoveClick(userId)
                confirmDeletion = null
            },
            onDismiss = { confirmDeletion = null }
        )
    }

    AdminSearchScreen(
        component = component,
        keyItem = UserItem::id,
        searchPlaceholder = "Найти пользователя",
        fabText = "Создать пользователя"
    ) { item ->
        val interactionSource = remember { MutableInteractionSource() }
        UserListItem(
            item = item,
            trailingContent = {
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
            },
            modifier = Modifier.hoverable(interactionSource)
        )
    }
}