package com.denchic45.studiversity.ui.studygroups

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.domain.model.StudyGroupItem

@Composable
fun StudyGroupListItem(
    item: StudyGroupItem,
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