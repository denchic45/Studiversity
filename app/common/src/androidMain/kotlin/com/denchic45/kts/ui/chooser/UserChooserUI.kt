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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.theme.spacing

@Composable
fun UserChooserScreen(component: UserSearchComponent, appBarInteractor: AppBarInteractor) {
    SearchScreen(
        component = component,
        appBarInteractor = appBarInteractor,
        keyItem = UserItem::id,
        itemContent = { UserListItem(it) }
    )
}

@Composable
fun UserListItem(
    item: UserItem,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null
) {
    ListItem(
        headlineContent = { Text(item.title) },
        leadingContent = {
            AsyncImage(
                model = item.avatarUrl,
                contentDescription = "user avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            )
        },
        modifier = modifier.padding(vertical = MaterialTheme.spacing.small),
        trailingContent = trailingContent
    )
}