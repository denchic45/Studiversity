package com.denchic45.studiversity.ui.member

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.ui.user.UserListItem

@Composable
fun MemberListItem(
    item: UserItem,
    allowEdit: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    UserListItem(
        item = item,
        trailingContent = if (allowEdit) {
            {
                if (isHovered) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onRemoveClick) {
                            Icon(Icons.Outlined.PersonRemove, null)
                        }

                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Outlined.Edit, null)
                        }
                    }
                }
            }
        } else null,
        modifier = Modifier.clickable(
            onClick = onClick,
            interactionSource = interactionSource,
            indication = rememberRipple()
        )
    )
}