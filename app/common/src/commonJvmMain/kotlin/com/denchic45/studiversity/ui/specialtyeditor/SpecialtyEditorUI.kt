package com.denchic45.studiversity.ui.specialtyeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.denchic45.studiversity.ui.LoadingDialog
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.TitleAlertDialog
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

    ResourceContent(
        resource = stateResource,
        onLoading = {
            LoadingDialog(onDismissRequest = component::onDismissRequest)
        }) { state ->
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
                TitleAlertDialog(
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
//        Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            placeholder = { Text("Название") },
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
        Row {
            OutlinedTextField(
                value = state.code,
                onValueChange = onCodeChange,
                placeholder = { Text("Код") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.shortname,
                onValueChange = onShortnameChange,
                placeholder = { Text("Краткое название") },
                modifier = Modifier.fillMaxWidth()
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