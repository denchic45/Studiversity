package com.denchic45.studiversity.ui.course

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.model.CourseItem

@Composable
fun CourseListItem(
    item: CourseItem,
    onClick: () -> Unit = {},
    trailingContent: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
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
        modifier = modifier.height(64.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                enabled = true,
                onClick = { onClick() }
            ).padding(horizontal = 16.dp)
    )
}