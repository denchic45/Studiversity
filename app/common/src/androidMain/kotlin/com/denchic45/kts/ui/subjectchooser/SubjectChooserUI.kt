package com.denchic45.kts.ui.subjectchooser

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage
import com.denchic45.kts.ui.chooser.ChooserScreen

@Composable
fun SubjectChooserScreen(component: SubjectChooserComponent) {
    ChooserScreen(component = component, keyItem = { it.id }, itemContent = {
        ListItem(
            headlineContent = { Text(it.name) },
            leadingContent = {
                AsyncImage(model = it.iconUrl, contentDescription = "subject icon")
            }
        )
    })
}