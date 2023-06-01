package com.denchic45.kts.ui.chooser

import androidx.compose.foundation.layout.padding
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
import coil.compose.AsyncImage
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.theme.spacing

@Composable
fun UserChooserScreen(component: UserChooserComponent) {
    SearchScreen(
        component = component,
        keyItem = UserItem::id,
        itemContent = { UserListItem(it) }
    )
}

@Composable
fun UserListItem(
    item: UserItem,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    ListItem(
        headlineContent = { Text(item.title,) },
        leadingContent = {
            UserAvatarImage(item.avatarUrl)
        },
        modifier = modifier.padding(vertical = MaterialTheme.spacing.small),
        trailingContent = trailingContent
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