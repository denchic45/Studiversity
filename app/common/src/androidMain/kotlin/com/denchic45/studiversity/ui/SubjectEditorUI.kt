package com.denchic45.studiversity.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.common.R
import com.denchic45.studiversity.ui.subjecteditor.SubjectEditorComponent
import com.denchic45.studiversity.ui.subjecticons.SubjectIconsDialog
import com.denchic45.studiversity.ui.theme.spacing

@Composable
fun SubjectEditorDialog(component: SubjectEditorComponent) {
    val viewStateResource by component.viewState.collectAsState()
    val allowSave by component.allowSave.collectAsState()

    ResourceContent(
        resource = viewStateResource,
        onLoading = { LoadingDialog(onDismissRequest = component::onCancel) }
    ) {
        AlertDialog(
            onDismissRequest = component::onCancel,
            title = {
                Text(text = if (component.isNew) "Новый предмет" else "Редактировать предмет")
            },
            text = {
                SubjectEditorContent(
                    state = it,
                    onNameType = component::onNameType,
                    onShortnameType = component::onShortnameType,
                    onIconClick = component::onIconClick
                )
            },
            confirmButton = {
                TextButton(enabled = allowSave, onClick = component::onSaveClick) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = component::onCancel) {
                    Text("Отменить")
                }
            }
        )
    }

    val childOverlay by component.childOverlay.subscribeAsState()
    childOverlay.overlay?.let {
        SubjectIconsDialog(it.instance)
    }
}

@Composable
fun SubjectEditorContent(
    state: SubjectEditorComponent.EditableSubjectState,
    onNameType: (String) -> Unit,
    onShortnameType: (String) -> Unit,
    onIconClick: () -> Unit
) {
    Column() {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SubcomposeAsyncImage(
                model = state.iconUrl?.let { url ->
                    ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .decoderFactory(SvgDecoder.Factory())
                        .build()

                } ?: R.drawable.ic_subject,
                loading = { CircularProgressIndicator() },
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .padding(MaterialTheme.spacing.small)
                    .clickable(onClick = onIconClick)
            )

            OutlinedTextField(
                value = state.name,
                onValueChange = onNameType,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
        OutlinedTextField(value = state.shortname,
            onValueChange = onShortnameType,
            placeholder = { Text("Дополнительное название") }
        )
    }
}