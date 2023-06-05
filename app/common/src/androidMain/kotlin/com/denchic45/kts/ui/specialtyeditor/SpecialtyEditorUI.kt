package com.denchic45.kts.ui.specialtyeditor

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
import com.denchic45.kts.ui.LoadingDialog
import com.denchic45.kts.ui.ResourceContent
import com.denchic45.kts.ui.theme.spacing


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
                Text(if (state.isNew) "Новая специальность" else "Редактировать специальности")
            },
            text = {
                SpecialtyEditorContent(
                    viewState = state,
                    onNameType = component::onNameType,
                    onShortnameType = component::onShortnameType
                )
            }
        )
    }
}

@Composable
fun SpecialtyEditorContent(
    viewState: SpecialtyEditorComponent.EditingSpecialty,
    onNameType: (String) -> Unit,
    onShortnameType: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
        OutlinedTextField(
            value = viewState.name,
            onValueChange = onNameType,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            placeholder = { Text("Название") },
            modifier = Modifier.focusRequester(focusRequester)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
        OutlinedTextField(
            value = viewState.shortname,
            onValueChange = onShortnameType,
            placeholder = { Text("Дополнительное название") }
        )
    }
}