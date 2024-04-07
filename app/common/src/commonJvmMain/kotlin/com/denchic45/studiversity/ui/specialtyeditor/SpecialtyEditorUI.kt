package com.denchic45.studiversity.ui.specialtyeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.component.AlertDialogTitle
import com.denchic45.studiversity.ui.confirm.DismissChangesConfirmDialog
import com.denchic45.studiversity.ui.specialty.RemoveSpecialtyConfirmDialog
import com.denchic45.studiversity.ui.theme.spacing


@Composable
fun SpecialtyEditorDialog(component: SpecialtyEditorComponent) {
    val stateResource by component.stateResource.collectAsState()
    val needToConfirmToDismiss by component.needToConfirmToDismiss.collectAsState()

    DismissChangesConfirmDialog(
        show = needToConfirmToDismiss,
        onConfirm = component::onDismissRequest,
        onDismiss = component::onCancelDismiss
    )

    stateResource.onSuccess { state ->
        AlertDialog(
            onDismissRequest = component::onDismissRequest,
            confirmButton = {
                TextButton(
                    enabled = state.allowSave,
                    onClick = component::onSaveClick
                ) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = component::onDismissRequest) { Text("Отмена") }
            },
            title = {
                AlertDialogTitle(
                    text = if (state.isNew) "Создать специальность" else "Изменить специальность",
                    onClose = component::onDismissRequest
                )
            },
            text = {
                SpecialtyEditorContent(
                    state = state,
                    onNameChange = component::onNameChange,
                    onCodeChange = component::onCodeChange,
                    onShortnameChange = component::onShortnameChange,
                    onRemoveClick = component::onRemoveClick
                )
            }
        )
    }
}

@Composable
fun SpecialtyEditorContent(
    state: EditingSpecialty,
    onNameChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onShortnameChange: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column {
        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            placeholder = { Text("Название") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
        Row {
            OutlinedTextField(
                value = state.code,
                onValueChange = onCodeChange,
                placeholder = { Text("Код") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(MaterialTheme.spacing.normal))
            OutlinedTextField(
                value = state.shortname,
                onValueChange = onShortnameChange,
                placeholder = { Text("Краткое название") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        if (!state.isNew) {
            var showConfirmDeletion by remember { mutableStateOf(false) }

            ListItem(
                headlineContent = { Text("Удалить специальность") },
                supportingContent = { Text("Восстановление будет невозможно") },
                leadingContent = {
                    Icon(
                        Icons.Outlined.Delete,
                        null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                modifier = Modifier.padding(top = MaterialTheme.spacing.normal)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { showConfirmDeletion = true }
            )
            if (showConfirmDeletion)
                RemoveSpecialtyConfirmDialog(
                    onConfirm = {
                        showConfirmDeletion = false
                        onRemoveClick()
                    },
                    onDismiss = {
                        showConfirmDeletion = false
                    }
                )

        }
    }
}