package com.denchic45.studiversity.ui.course

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.model.CourseItem

@Composable
fun CourseListItem(
    item: CourseItem,
    trailingContent: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = { Text(item.name) },
        supportingContent = if (item.archived) {
            { Text("В архиве") }
        } else null,
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.School,
                contentDescription = "course icon"
            )
        },
        trailingContent = trailingContent,
        modifier = modifier.height(64.dp).padding(horizontal = 16.dp)
    )
}