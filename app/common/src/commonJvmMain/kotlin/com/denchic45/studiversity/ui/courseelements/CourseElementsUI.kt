package com.denchic45.studiversity.ui.courseelements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.api.course.element.model.CourseElementResponse
import com.denchic45.studiversity.api.course.element.model.CourseMaterial
import com.denchic45.studiversity.api.course.element.model.CourseWork
import com.denchic45.studiversity.util.toString

@Composable
fun CourseElementListItem(item: CourseElementResponse, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(height = 64.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = { onClick() })
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = when (item.details) {
                    is CourseWork -> Icons.Outlined.Assignment
                    CourseMaterial -> Icons.Outlined.MenuBook
                },
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                item.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            when (val details = item.details) {
                is CourseWork -> details.dueDate?.let {
                    Text(
                        text = it.toString("dd MMM"), style = MaterialTheme.typography.bodySmall,
                        color = if (details.late) MaterialTheme.colorScheme.error else Color.Unspecified
                    )
                }

                is CourseMaterial -> {}
            }
        }
    }
}