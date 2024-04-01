package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.domain.model.CourseItem
import com.denchic45.studiversity.ui.course.ArchiveCourseConfirmDialog
import com.denchic45.studiversity.ui.course.CourseListItem
import com.denchic45.studiversity.ui.course.RemoveCourseConfirmDialog
import com.denchic45.studiversity.ui.course.RestorationCourseConfirmDialog
import com.denchic45.studiversity.ui.courseeditor.CourseEditorDialog
import java.util.*

@Composable
fun CoursesAdminScreen(component: CoursesAdminComponent) {
    val childSlot by component.childSlot.subscribeAsState()

    childSlot.child?.let {
        when (val child = it.instance) {
            is CoursesAdminComponent.Child.CourseEditor -> CourseEditorDialog(child.component)
        }
    }

    var confirmDeletion by remember { mutableStateOf<UUID?>(null) }
    confirmDeletion?.let { courseId ->
        RemoveCourseConfirmDialog(
            onConfirm = {
                component.onRemoveClick(courseId)
                confirmDeletion = null
            },
            onDismiss = {
                confirmDeletion = null
            }
        )
    }

    var confirmArchiving by remember { mutableStateOf<UUID?>(null) }
    confirmArchiving?.let { courseId ->
        ArchiveCourseConfirmDialog(
            onConfirm = {
                component.onArchiveClick(courseId)
                confirmArchiving = null
            },
            onDismiss = {
                confirmArchiving = null
            }
        )
    }

    var confirmRestoration by remember { mutableStateOf<UUID?>(null) }
    confirmRestoration?.let { courseId ->
        RestorationCourseConfirmDialog(
            onConfirm = {
                component.onUnarchiveClick(courseId)
                confirmRestoration = null
            },
            onDismiss = {
                confirmRestoration = null
            }
        )
    }

    AdminSearchScreen(
        component = component,
        keyItem = CourseItem::id,
        searchPlaceholder = "Найти курс",
        fabText = "Создать курс"
    ) { item ->
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()
        CourseListItem(
            item = item,
            onClick = { component.onSelect(item) },
            trailingContent = {
                if (isHovered) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { confirmDeletion = item.id }) {
                            Icon(Icons.Outlined.Delete, null)
                        }
                        if (!item.archived) {
                            IconButton(onClick = { confirmArchiving = item.id }) {
                                Icon(Icons.Outlined.Archive, null)
                            }
                        } else {
                            IconButton(onClick = { confirmRestoration = item.id }) {
                                Icon(Icons.Outlined.Unarchive, null)
                            }
                        }
                        IconButton(onClick = { component.onEditClick(item.id) }) {
                            Icon(Icons.Outlined.Edit, null)
                        }
                    }
                }
            }
        )
    }
}