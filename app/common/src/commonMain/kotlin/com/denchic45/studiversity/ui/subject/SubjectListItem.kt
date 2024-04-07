package com.denchic45.studiversity.ui.subject

import androidx.compose.foundation.layout.height
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.api.course.subject.model.SubjectResponse

@Composable
fun SubjectListItem(
    item: SubjectResponse,
    trailingContent: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = { Text(item.name) },
        trailingContent = trailingContent,
        modifier = modifier.height(64.dp)
    )
}