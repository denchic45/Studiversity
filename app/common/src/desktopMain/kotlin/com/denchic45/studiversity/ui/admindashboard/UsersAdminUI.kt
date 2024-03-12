package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.ui.model.UserItem
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun UsersAdminScreen(component: UsersAdminComponent) {
    val interactionSource = remember { MutableInteractionSource() }
    AdminSearchScreen(component, UserItem::id) { userItem ->
        UserListItem(
            item = userItem,
            onClick = { component.onUserClick(userItem.id) },
            trailingContent = {
                val isHovered by interactionSource.collectIsHoveredAsState()
                if (isHovered)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { component.onEditClick(userItem.id) }) {
                            Icon(Icons.Outlined.Edit, null)
                        }
                    }
            },
            interactionSource = interactionSource
        )
    }
}

@Composable
private fun UserListItem(
    item: UserItem,
    onClick: () -> Unit,
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
            leadingContent = {
                KamelImage(
                    resource = asyncPainterResource(avatarUrl),
                    null,
                    Modifier.size(40.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            },
            trailingContent = trailingContent,
            modifier = modifier
                .height(64.dp)
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
        )
//        Row(
//            modifier = modifier
//                .clip(RoundedCornerShape(16.dp))
//                .height(64.dp)
//                .clickable(onClick = onClick)
//                .padding(horizontal = 16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            KamelImage(
//                resource = asyncPainterResource(avatarUrl),
//                null,
//                Modifier.size(40.dp).clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//            Column(Modifier.padding(start = MaterialTheme.spacing.normal)) {
//                Text(title, style = MaterialTheme.typography.bodyLarge)
//                subtitle?.let { Text(it, style = MaterialTheme.typography.labelMedium) }
//            }
//        }
    }
}