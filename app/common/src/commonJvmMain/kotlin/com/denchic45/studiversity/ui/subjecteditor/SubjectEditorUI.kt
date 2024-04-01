package com.denchic45.studiversity.ui.subjecteditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.ui.LoadingDialog
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.DeleteListItem
import com.denchic45.studiversity.ui.subject.RemoveSubjectConfirmDialog
import com.denchic45.studiversity.ui.theme.spacing

@Composable
fun SubjectEditorDialog(component: SubjectEditorComponent) {
    val viewStateResource by component.viewState.collectAsState()
    val allowSave by component.allowSave.collectAsState()

    ResourceContent(
        resource = viewStateResource,
        onLoading = { LoadingDialog(onDismissRequest = component::onCancel) }
    ) { state ->
        AlertDialog(
            onDismissRequest = component::onCancel,
            title = {
                Text(text = if (state.isNew) "Новый предмет" else "Редактировать предмет")
            },
            text = {
                SubjectEditorContent(
                    state = state,
                    avatarContent = { // todo ПОТОМ ВЕРНУТЬ
//                        SubcomposeAsyncImage(
//                            model = state.iconUrl?.let { url ->
//                                ImageRequest.Builder(PlatformContext.INSTANCE)
//                                    .data(url)
////                                    .decoderFactory(SvgDecoder.Factory())
//                                    .build()
//
//                            }
////                                ?: R.drawable.ic_subject,
//                                ?: Icons.Outlined.Subject,
//                            loading = { CircularProgressIndicator() },
//                            contentDescription = null,
//                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.secondary),
//                            modifier = Modifier
//                                .size(56.dp)
//                                .clip(RoundedCornerShape(16.dp))
//                                .padding(MaterialTheme.spacing.small)
//                                .clickable(onClick = component::onIconClick)
//                        )
                    },
                    onNameChange = component::onNameType,
                    onShortnameChange = component::onShortnameType,
                    onRemoveClick = component::onRemoveClick
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

    val childSlot by component.childSlot.subscribeAsState()
//    childSlot.child?.let { todo потом вернуть
//        SubjectIconsDialog(it.instance)
//    }
}

@Composable
fun SubjectEditorContent(
    state: EditingSubjectState,
    avatarContent: @Composable () -> Unit,
    onNameChange: (String) -> Unit,
    onShortnameChange: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            avatarContent()
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
        OutlinedTextField(value = state.shortname,
            onValueChange = onShortnameChange,
            placeholder = { Text("Дополнительное название") }
        )

        if (!state.isNew) {
            var showDeleteConfirmation by remember { mutableStateOf(false) }

            DeleteListItem(
                title = "Удалить предмет",
                text = "Восстановление будет невозможно",
                onClick = { showDeleteConfirmation = true }
            )

            if (showDeleteConfirmation)
                RemoveSubjectConfirmDialog(
                    onConfirm = {
                        showDeleteConfirmation = false
                        onRemoveClick()
                    },
                    onDismiss = { showDeleteConfirmation = false }
                )
        }
    }
}