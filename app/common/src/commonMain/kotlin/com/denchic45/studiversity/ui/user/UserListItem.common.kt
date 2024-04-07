package com.denchic45.studiversity.ui.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.denchic45.studiversity.domain.model.UserItem

@Composable
fun UserListItem(
    item: UserItem,
    trailingContent: (@Composable () -> Unit)? = null,
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
            leadingContent = { UserAvatarImage(item.avatarUrl) },
            trailingContent = trailingContent,
            modifier = modifier.height(64.dp)
        )
    }
}

@Composable
fun UserAvatarImage(url: String) {
    AsyncImage(
        model = url,
        contentDescription = "user avatar",
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}