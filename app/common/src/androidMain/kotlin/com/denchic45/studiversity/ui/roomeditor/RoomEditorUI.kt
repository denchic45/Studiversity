package com.denchic45.studiversity.ui.roomeditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.denchic45.studiversity.ui.LoadingDialog
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.theme.spacing


@Composable
fun RoomEditorDialog(component: RoomEditorComponent) {
    val stateResource by component.stateResource.collectAsState()
    ResourceContent(
        resource = stateResource,
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
                Text(if (state.isNew) "Новая аудитория" else "Редактировать аудиторию")
            },
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
            placeholder = { Text("Дополнительное название") }
        )
    }
}