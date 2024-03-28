package com.denchic45.studiversity.ui.specialtyeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.denchic45.studiversity.ui.theme.spacing


@Composable
fun SpecialtyEditorDialog(component: SpecialtyEditorComponent) {
    val viewStateResource by component.viewState.collectAsState()
    ResourceContent(
        resource = viewStateResource,
        onLoading = {
            LoadingDialog(onDismissRequest = component::onCloseClick)
        }) { state ->
        AlertDialog(
            onDismissRequest = component::onCloseClick,
            confirmButton = {
                TextButton(
                    enabled = state.allowSave,
                    onClick = component::onSaveClick
                ) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = component::onCloseClick) { Text("Отмена") }
            },
            title = {
                Text(if (state.isNew) "Создать специальность" else "Редактировать специальности")
            },
            text = {
                SpecialtyEditorContent(
                    state = state,
                    onNameChange = component::onNameType,
                    onShortnameChange = component::onShortnameType,
                    onRemoveClick = component::onRemoveClick
                )
            }
        )
    }
}

@Composable
fun SpecialtyEditorContent(
    state: SpecialtyEditorComponent.EditingSpecialty,
    onNameChange: (String) -> Unit,
    onShortnameChange: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            placeholder = { Text("Название") },
            modifier = Modifier.focusRequester(focusRequester)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
        OutlinedTextField(
            value = state.shortname,
            onValueChange = onShortnameChange,
            placeholder = { Text("Дополнительное название") }
        )

        if (!state.isNew) {
            var confirmRemove by remember { mutableStateOf(false) }

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
                    .clickable { confirmRemove = true }
            )
            if (confirmRemove)
                AlertDialog(
                    onDismissRequest = { confirmRemove = false },
                    icon = { Icon(Icons.Outlined.Delete, null) },
                    title = { Text("Удалить пользователя?") },
                    text = {
                        Text(
                            """
                                Удалятся все группы, с данной специальностью.
                                Восстановить пользователя будет невозможно.
                            """.trimIndent()
                        )
                    },
                    confirmButton = {
                        FilledTonalButton(onClick = {
                            confirmRemove = false
                            onRemoveClick()
                        }) { Text("Удалить") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            confirmRemove = false
                        }) { Text("Отмена") }
                    }
                )
        }
    }
}