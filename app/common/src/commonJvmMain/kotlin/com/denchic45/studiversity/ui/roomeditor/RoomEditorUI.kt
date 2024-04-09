package com.denchic45.studiversity.ui.roomeditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.theme.spacing


@Composable
fun RoomEditorDialog(component: RoomEditorComponent) {
    val stateResource by component.stateResource.collectAsState()

    stateResource.onSuccess { state ->
        AlertDialog(
            onDismissRequest = component::onCloseClick,
            confirmButton = {
                TextButton(
                    enabled = state.allowSave,
                    onClick = component::onSaveClick
                ) { Text("Сохранить") }
            },
            dismissButton = { TextButton(onClick = component::onCloseClick) { Text("Отмена") } },
            title = { Text(if (state.isNew) "Новая аудитория" else "Редактировать аудиторию") },
            text = {
                RoomEditorContent(
                    state = state,
                    onNameChange = component::onNameChange,
                    onShortnameChange = component::onShortnameChange
                )
            }
        )
    }
}

@Composable
fun RoomEditorContent(
    state: EditingRoom,
    onNameChange: (String) -> Unit,
    onShortnameChange: (String) -> Unit
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
            placeholder = { Text("Краткое название") }
        )
    }
}