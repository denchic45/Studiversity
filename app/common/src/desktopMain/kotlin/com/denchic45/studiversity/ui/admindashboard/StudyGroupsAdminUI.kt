package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.domain.model.StudyGroupItem
import com.denchic45.studiversity.ui.studygroup.RemoveStudyGroupConfirmDialog
import com.denchic45.studiversity.ui.studygroup.StudyGroupListItem
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorDialog
import java.util.*

@Composable
fun StudyGroupsAdminScreen(component: StudyGroupsAdminComponent) {
    val childSlot by component.childSlot.subscribeAsState()

    childSlot.child?.let {
        when (val child = it.instance) {
            is StudyGroupsAdminComponent.Child.StudyGroupEditor -> StudyGroupEditorDialog(child.component)
        }
    }

    var confirmDeletion by remember { mutableStateOf<UUID?>(null) }
    confirmDeletion?.let { userId ->
        RemoveStudyGroupConfirmDialog(
            onConfirm = {
                component.onRemoveClick(userId)
                confirmDeletion = null
            },
            onDismiss = { confirmDeletion = null }
        )
    }

    AdminSearchScreen(
        component = component,
        keyItem = StudyGroupItem::id,
        searchPlaceholder = "Найти группу",
        fabText = "Создать группу"
    ) { item ->
        val interactionSource = remember { MutableInteractionSource() }
        StudyGroupListItem(item, trailingContent = {
            val isHovered by interactionSource.collectIsHoveredAsState()
            if (isHovered)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { confirmDeletion = item.id }) {
                        Icon(Icons.Outlined.Delete, null)
                    }
                    IconButton(onClick = { component.onEditClick(item.id) }) {
                        Icon(Icons.Outlined.Edit, null)
                    }
                }
        }, interactionSource = interactionSource)
    }
}