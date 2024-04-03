package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.foundation.hoverable
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
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.api.course.subject.model.SubjectResponse
import com.denchic45.studiversity.ui.subject.RemoveSubjectConfirmDialog
import com.denchic45.studiversity.ui.subject.SubjectListItem
import com.denchic45.studiversity.ui.subjecteditor.SubjectEditorDialog
import java.util.*

@Composable
fun SubjectsAdminScreen(component: SubjectsAdminComponent) {
    val childSlot by component.childSlot.subscribeAsState()

    childSlot.child?.let {
        SubjectEditorDialog(it.instance.component)
    }

    var confirmDeletion by remember { mutableStateOf<UUID?>(null) }
    confirmDeletion?.let { userId ->
        RemoveSubjectConfirmDialog(
            onConfirm = {
                component.onRemoveClick(userId)
                confirmDeletion = null
            },
            onDismiss = { confirmDeletion = null }
        )
    }

    AdminSearchScreen(
        component = component,
        keyItem = SubjectResponse::id,
        searchPlaceholder = "Найти предмет",
        fabText = "Создать предмет"
    ) { item ->
        val interactionSource = remember { MutableInteractionSource() }
        SubjectListItem(
            item, trailingContent = {
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
            }, modifier = Modifier.hoverable(interactionSource)
        )
    }
}