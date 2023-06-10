package com.denchic45.studiversity.ui.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse

@Composable
fun StudyGroupChooserScreen(
    component: StudyGroupChooserComponent,
) {
    SearchScreen(
        component = component,
        keyItem = { it.id },
        itemContent = {
            StudyGroupListItem(it)
        })
}

@Composable
fun StudyGroupListItem(
    item: StudyGroupResponse,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null
) {
    ListItem(
        headlineContent = { Text(item.name) },
        leadingContent = {
            Icon(imageVector = Icons.Outlined.Group, contentDescription = "group icon")
        },
        trailingContent = trailingContent,
        modifier = modifier,
    )
}