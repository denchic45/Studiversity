package com.denchic45.studiversity.ui.user

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
    UserListItem(
        item = item,
        avatarContent = { UserAvatarImage(item.avatarUrl) },
        trailingContent = trailingContent,
        modifier = modifier
    )
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