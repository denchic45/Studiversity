package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.domain.model.StudyGroupItem
import com.denchic45.studiversity.ui.component.StudyGroupListItem
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorDialog

@Composable
fun StudyGroupsAdminScreen(component: StudyGroupsAdminComponent) {
    val childSlot by component.childSlot.subscribeAsState()

    childSlot.child?.let {
        when (val child = it.instance) {
            is StudyGroupsAdminComponent.Child.StudyGroupEditor -> StudyGroupEditorDialog(child.component)
        }
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
                    IconButton(onClick = { component.onEditClick(item.id) }) {
                        Icon(Icons.Outlined.Edit, null)
                    }
                }
        }, interactionSource = interactionSource)
    }
}