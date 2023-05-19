package com.denchic45.kts.ui.specialtyeditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.ResourceContent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialtyEditorDialog(component: SpecialtyEditorComponent) {
    val viewStateResource by component.viewState.collectAsState()
    ResourceContent(viewStateResource, onLoading = {
        AlertDialog(onDismissRequest = component::onCloseClick) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }) { state ->
        AlertDialog(
            onDismissRequest = component::onCloseClick,
            confirmButton = {
                TextButton(
                    enabled = state.saveEnabled,
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
    Column {
        OutlinedTextField(
            value = viewState.name,
            onValueChange = onNameType,
        )
        OutlinedTextField(
            value = viewState.shortname,
            onValueChange = onShortnameType,
        )
    }
}