package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.model.CourseItem
import com.denchic45.studiversity.ui.component.CourseListItem

@Composable
fun CoursesAdminScreen(component: CoursesAdminComponent) {
    AdminSearchScreen(
        component = component,
        keyItem = CourseItem::id,
        searchPlaceholder = "Найти курс",
        fabText = "Создать курс"
    ) { item ->
        CourseListItem(
            item = item,
            onClick = { component.onSelect(item) },
            onEditClick = { component.onEditClick(item.id) })
    }
}

@Composable
private fun CourseListItem(
    item: CourseItem,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    CourseListItem(
        item = item,
        modifier = Modifier.run {
            if (isHovered) background(MaterialTheme.colorScheme.secondaryContainer, shape)
            else this
        }.clip(shape)
            .height(64.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                enabled = true,
                onClick = { onClick() }
            ).padding(horizontal = 16.dp), trailingContent = {

            if (isHovered) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onEditClick() }) {
                        Icon(Icons.Outlined.Edit, null)
                    }
                }
            }
        }
    )
}