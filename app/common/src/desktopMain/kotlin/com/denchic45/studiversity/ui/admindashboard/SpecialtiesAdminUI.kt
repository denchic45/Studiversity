package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.studiversity.ui.specialtyeditor.SpecialtyEditorDialog

@Composable
fun SpecialtiesAdminScreen(component: SpecialtiesAdminComponent) {
    val childSlot by component.childSlot.subscribeAsState()

    childSlot.child?.let {
        when (val child = it.instance) {
            is SpecialtiesAdminComponent.Child.SpecialtyEditor -> SpecialtyEditorDialog(child.component)
        }
    }

    AdminSearchScreen(
        component = component,
        keyItem = SpecialtyResponse::id,
        searchPlaceholder = "Найти специальность",
        fabText = "Создать специальность"
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()

        ListItem(
            headlineContent = { Text(it.name) },
            trailingContent = {
                if (isHovered) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { component.onEditClick(it.id) }) {
                            Icon(Icons.Outlined.Edit, null)
                        }
                    }
                }
            }
        )
    }
}