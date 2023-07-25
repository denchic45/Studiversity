package com.denchic45.studiversity.ui.coursematerialeditor

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.onLoading
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.DropdownMenuItem
import com.denchic45.studiversity.ui.appbar.ActionMenuItem2
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.updateAppBarState
import com.denchic45.studiversity.ui.asString
import com.denchic45.studiversity.ui.attachment.AttachmentListItem
import com.denchic45.studiversity.ui.component.HeaderItemUI
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.ui.theme.AppTheme
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.uiIconOf
import com.denchic45.studiversity.util.AttachmentViewer
import com.denchic45.studiversity.util.OpenMultipleAnyDocuments
import com.denchic45.studiversity.util.collectWithLifecycle
import com.denchic45.studiversity.util.findActivity
import com.denchic45.studiversity.util.getFile

@Composable
fun CourseMaterialEditorScreen(component: CourseMaterialEditorComponent) {

    val context = LocalContext.current
    val attachmentViewer by lazy {
        AttachmentViewer(context.findActivity()) {
            Toast.makeText(
                context,
                "Невозможно открыть файл на данном устройстве",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    val pickFileLauncher = rememberLauncherForActivityResult(OpenMultipleAnyDocuments()) { uris ->
        component.onFilesSelect(uris.map { it.getFile(context) })
    }

    component.openAttachment.collectWithLifecycle {
        attachmentViewer.openAttachment(it)
    }

    val state by component.viewState.collectAsState()
    val attachments by component.attachmentItems.collectAsState()
    val allowSave by component.allowSave.collectAsState()

    updateAppBarState(
        allowSave, AppBarContent(
            actionItems = listOf(
                ActionMenuItem2(
                    icon = uiIconOf(Icons.Default.Done),
                    enabled = allowSave,
                    onClick = component::onSaveClick
                )
            )
        )
    )

    CourseMaterialEditorContent(
        stateResource = state,
        attachmentsResource = attachments,
        onNameType = component::onNameType,
        onDescriptionType = component::onDescriptionType,
        onTopicNameType = component::onTopicNameType,
        onTopicSelect = component::onTopicSelect,
        onAttachmentAdd = { pickFileLauncher.launch(Unit) },
        onAttachmentClick = component::onAttachmentClick,
        onAttachmentRemove = component::onAttachmentRemove
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseMaterialEditorContent(
    stateResource: Resource<CourseMaterialEditorComponent.EditingMaterial>,
    attachmentsResource: Resource<List<AttachmentItem>>,
    onNameType: (String) -> Unit,
    onDescriptionType: (String) -> Unit,
    onTopicNameType: (String) -> Unit,
    onTopicSelect: (DropdownMenuItem) -> Unit,
    onAttachmentAdd: () -> Unit,
    onAttachmentClick: (item: AttachmentItem) -> Unit,
    onAttachmentRemove: (position: Int) -> Unit
) {
    val scrollState = rememberScrollState()
    Surface(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        stateResource.onSuccess { state ->
            Column {
                var showDatePicker by remember { mutableStateOf(false) }
                var showTimePicker by remember { mutableStateOf(false) }

                Column(Modifier.padding(MaterialTheme.spacing.normal)) {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { onNameType(it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = state.nameMessage != null,
                        placeholder = { Text("Название") },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        supportingText = { Text(state.nameMessage ?: "") }
                    )
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = { onDescriptionType(it) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = state.nameMessage != null,
                        placeholder = { Text("Описание (необязательно)") },
                    )
                }

                Spacer(Modifier.height(8.dp))

                var expanded by remember { mutableStateOf(false) }
                val showList = expanded && state.foundTopics.isNotEmpty()

                ExposedDropdownMenuBox(
                    expanded = showList,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.normal)
                ) {
                    OutlinedTextField(
                        value = state.topicQueryText.takeIf(String::isNotEmpty)
                            ?: state.selectedTopic?.title?.asString() ?: "",
                        onValueChange = { onTopicNameType(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        label = { Text("Раздел") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = showList
                            )
                        },
                        singleLine = true,
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = showList,
                        onDismissRequest = { expanded = false }) {
                        state.foundTopics.forEach {
                            DropdownMenuItem(
                                text = { Text(it.title.asString()) },
                                onClick = {
                                    onTopicSelect(it)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                ListItem(
                    headlineContent = { Text("Добавить файл") },
                    modifier = Modifier.clickable(onClick = onAttachmentAdd),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Attachment,
                            contentDescription = "add attachment"
                        )
                    }
                )

                attachmentsResource.onSuccess { attachmentItems ->
                    if (attachmentItems.isNotEmpty()) {
                        HeaderItemUI(
                            name = "Прикрепленные файлы",
                            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.normal)
                        )
                        LazyRow(contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.normal)) {
                            itemsIndexed(
                                items = attachmentItems,
                                key = { _, item -> item.attachmentId }) { index, item ->
                                AttachmentListItem(
                                    item = item,
                                    onClick = { onAttachmentClick(item) },
                                    onRemove = { onAttachmentRemove(index) })
                            }
                        }
                    }
                }
            }
        }.onLoading {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Выберите время",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            toggle()
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Отменить") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("ОК") }
                }
            }
        }
    }
}

@Preview
@Composable
fun CourseMaterialEditorPreview() {
    AppTheme {
        CourseMaterialEditorContent(
            stateResource = Resource.Success(CourseMaterialEditorComponent.EditingMaterial().apply {
                name = "Контрольная работа"
            }),
            attachmentsResource = Resource.Success(emptyList()),
            onNameType = {},
            onDescriptionType = {},
            onTopicNameType = {},
            onTopicSelect = {},
            onAttachmentAdd = {},
            onAttachmentClick = {},
            onAttachmentRemove = {}
        )
    }
}