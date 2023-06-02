package com.denchic45.kts.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.denchic45.kts.R
import com.denchic45.kts.ui.subjecteditor.SubjectEditorComponent
import com.denchic45.kts.ui.theme.spacing

@Composable
fun SubjectEditorDialog(component: SubjectEditorComponent) {
    val viewStateResource by component.viewState.collectAsState()
    val allowSave by component.allowSave.collectAsState()
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        title = {},
        text = {},
        confirmButton = {
            TextButton(enabled = allowSave, onClick = component::onSaveClick) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(enabled = allowSave, onClick = component::onCancel) {
                Text("Отменить")
            }
        })
    ResourceContent(resource = viewStateResource) {
        SubjectEditorContent(
            state = it,
            onNameType = component::onNameType,
            onShortnameType = component::onShortnameType,
            onIconClick = component::onIconClick
        )
    }
}

@Composable
fun SubjectEditorContent(
    state: SubjectEditorComponent.EditableSubjectState,
    onNameType: (String) -> Unit,
    onShortnameType: (String) -> Unit,
    onIconClick: () -> Unit
) {
    Column {
        Row {
            IconButton(onClick = onIconClick) {
                Image(
                    painter = state.iconUrl?.let { url ->
                        rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(url)
                                .decoderFactory(SvgDecoder.Factory())
                                .build()
                        )
                    } ?: painterResource(R.drawable.ic_subject),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )

            }
            OutlinedTextField(value = state.name, onValueChange = onNameType)
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
        OutlinedTextField(value = state.shortname, onValueChange = onShortnameType)
    }
}