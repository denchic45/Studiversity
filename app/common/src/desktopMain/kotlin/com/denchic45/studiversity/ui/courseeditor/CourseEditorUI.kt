package com.denchic45.studiversity.ui.courseeditor

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.TitleAlertDialog
import com.denchic45.studiversity.ui.confirm.DismissChangesConfirmDialog

@Composable
fun CourseEditorDialog(component: CourseEditorComponent) {
    val stateResource by component.stateResource.collectAsState()
    val allowSave by component.allowSave.collectAsState()
    val needToConfirmToDismiss by component.needToConfirmToDismiss.collectAsState()

    DismissChangesConfirmDialog(
        show = needToConfirmToDismiss,
        onConfirm = component::onDismissRequest,
        onDismiss = component::onCancelDismiss
    )

    ResourceContent(stateResource) { state ->
        AlertDialog(onDismissRequest = component::onDismissRequest,
            title = {
                TitleAlertDialog(
                    text = if (state.isNew) "Создать курс" else "Изменить курс",
                    onClose = component::onDismissRequest
                )
            },
            text = {
                CourseEditorContent(
                    state = state,
                    onNameChange = component::onNameChange,
                    onSubjectChoose = component::onSubjectChoose,
                    onSubjectClose = component::onSubjectClose
                )
            },
            confirmButton = { TextButton(component::onSaveClick, enabled = allowSave) { Text("Сохранить") } },
            dismissButton = { TextButton(component::onDismissRequest) { Text("Отмена") } }
        )
    }
}