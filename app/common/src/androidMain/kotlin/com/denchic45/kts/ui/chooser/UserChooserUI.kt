package com.denchic45.kts.ui.chooser

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage

@Composable
fun UserChooserScreen(component: UserChooserComponent) {
    ChooserScreen(component = component, keyItem = { it.id }, itemContent = {
        ListItem(
            headlineContent = { Text(it.fullName) },
            leadingContent = {
                AsyncImage(model = it.avatarUrl, contentDescription = "user avatar")
            }
        )
    })
}