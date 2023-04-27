package com.denchic45.kts.ui.chooser

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage

@Composable
fun StudyGroupChooserScreen(component: StudyGroupChooserComponent) {
    ChooserScreen(component = component, keyItem = { it.id }, itemContent = {
        ListItem(
            headlineContent = { Text(it.name) },
            leadingContent = {
                Icon(imageVector = Icons.Outlined.Group, contentDescription = "group icon")
            }
        )
    })
}