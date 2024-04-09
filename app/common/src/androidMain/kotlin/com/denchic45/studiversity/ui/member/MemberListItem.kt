package com.denchic45.studiversity.ui.member

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.ui.component.ExpandableDropdownMenu
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

    UserListItem(
        item = item,
        trailingContent = if (allowEdit) {
            {
                var expanded by remember { mutableStateOf(false) }
                ExpandableDropdownMenu(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }) {

                    DropdownMenuItem(
                        text = { Text("Изменить") },
                        onClick = {
                            expanded = false
                            onEditClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Удалить") },
                        onClick = {
                            expanded = false
                            onRemoveClick()
                        }
                    )
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