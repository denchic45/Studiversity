package com.denchic45.kts.ui.chooser

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.model.UserItem

@Composable
fun UserChooserScreen(component: UserChooserComponent, appBarInteractor: AppBarInteractor) {
    ChooserScreen(component = component,
        appBarInteractor = appBarInteractor,
        keyItem = { it.id },
        itemContent = { UserItemUI(it) }
    )
}

@Composable
fun UserItemUI(item: UserItem, modifier: Modifier = Modifier) {
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
        modifier = modifier
    )
}