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

@Composable
fun UserChooserScreen(component: UserChooserComponent) {
    ChooserScreen(component = component, keyItem = { it.id }, itemContent = {
        ListItem(
            headlineContent = { Text(it.fullName) },
            leadingContent = {
                AsyncImage(
                    model = it.avatarUrl,
                    contentDescription = "user avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                )
            }
        )
    })
}