package com.denchic45.studiversity.ui.subjecteditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.component.AlertDialogTitle
import com.denchic45.studiversity.ui.component.DeleteListItem
import com.denchic45.studiversity.ui.confirm.DismissChangesConfirmDialog
import com.denchic45.studiversity.ui.subject.RemoveSubjectConfirmDialog
import com.denchic45.studiversity.ui.theme.spacing

@Composable
fun SubjectEditorDialog(component: SubjectEditorComponent) {
    val stateResource by component.stateResource.collectAsState()
    val allowSave by component.allowSave.collectAsState()
    val needToConfirmToDismiss by component.needToConfirmToDismiss.collectAsState()

    DismissChangesConfirmDialog(
        show = needToConfirmToDismiss,
        onConfirm = component::onDismissRequest,
        onDismiss = component::onCancelDismiss
    )

    stateResource.onSuccess { state ->
        AlertDialog(
            onDismissRequest = component::onDismissRequest,
            title = {
                AlertDialogTitle(
                    text = if (state.isNew) "Новый предмет" else "Изменить предмет",
                    onClose = component::onDismissRequest
                )
            },
            text = {
                SubjectEditorContent(
                    state = state,
                    onNameChange = component::onNameChange,
                    onShortnameChange = component::onShortnameChange,
                    onRemoveClick = component::onRemoveClick
                )
            },
            confirmButton = {
                TextButton(enabled = allowSave, onClick = component::onSaveClick) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = component::onDismissRequest) {
                    Text("Отменить")
                }
            }
        )
    }
}

@Composable
fun SubjectEditorContent(
    state: EditingSubjectState,
    onNameChange: (String) -> Unit,
    onShortnameChange: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                placeholder = { Text("Название") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
        OutlinedTextField(
            value = state.shortname,
            onValueChange = onShortnameChange,
            placeholder = { Text("Краткое название") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (!state.isNew) {
            var showDeleteConfirmation by remember { mutableStateOf(false) }

            DeleteListItem(
                title = "Удалить предмет",
                text = "Восстановление будет невозможно",
                onClick = { showDeleteConfirmation = true }
            )

            if (showDeleteConfirmation)
                RemoveSubjectConfirmDialog(
                    onConfirm = {
                        showDeleteConfirmation = false
                        onRemoveClick()
                    },
                    onDismiss = { showDeleteConfirmation = false }
                )
        }
    }
}