package com.denchic45.kts.ui.specialtyeditor

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.kts.domain.onSuccess


@Composable
fun SpecialtyEditorDialog(component: SpecialtyEditorComponent) {
    val viewStateResource by component.viewState.collectAsState()
    viewStateResource.onSuccess { state->
        AlertDialog(onDismissRequest = component::onCloseClick,
            confirmButton = { Button(enabled = state.saveEnabled,onClick = {component.onSaveClick()}) {

            }})
    }
}

@Composable
fun SpecialtyEditorContent(
    viewState: SpecialtyEditorComponent.EditingSpecialty,
    onNameType:(String)->Unit,
    onShortnameType:(String)->Unit
) {

}