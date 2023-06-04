package com.denchic45.kts.ui.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.denchic45.stuiversity.api.course.model.CourseResponse

@Composable
fun CourseChooserScreen(component: CourseChooserComponent) {
    SearchScreen(
        component = component,
        keyItem = { it.id }, itemContent = {
            CourseListItem(it)
        })
}

@Composable
fun CourseListItem(
    item: CourseResponse,
    trailingContent: (@Composable () -> Unit)? = null
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
        trailingContent = trailingContent
    )
}