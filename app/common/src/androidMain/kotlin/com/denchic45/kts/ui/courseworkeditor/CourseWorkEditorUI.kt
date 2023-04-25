package com.denchic45.kts.ui.courseworkeditor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ContentAlpha
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.DropdownMenuItem
import com.denchic45.kts.ui.asString
import com.denchic45.kts.ui.component.HeaderItemUI
import com.denchic45.kts.ui.coursework.details.AttachmentItemUI
import com.denchic45.kts.ui.model.AttachmentItem
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.stuiversity.util.Dates
import com.denchic45.stuiversity.util.toString
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@Composable
fun CourseWorkEditorScreen(component: CourseWorkEditorComponent) {
    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {

        }
    }

    val state by component.viewState.collectAsState()
    val attachments by component.attachmentItems.collectAsState()

    CourseWorkEditorContent(
        stateResource = state,
        attachmentsResource = attachments,
        onNameType = component::onNameType,
        onDescriptionType = component::onDescriptionType,
        onTopicNameType = component::onTopicNameType,
        onTopicSelect = component::onTopicSelect,
        onAttachmentClick = {
            pickFileLauncher.launch("*/*")
        },
        onAttachmentRemove = component::onAttachmentRemove,
        onDueDateTimeSelect = component::onDueDateTimeSelect,
        onDueDateTimeClear = component::onDueDateTimeClear
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseWorkEditorContent(
    stateResource: Resource<CourseWorkEditorComponent.EditingWork>,
    attachmentsResource: Resource<List<AttachmentItem>>,
    onNameType: (String) -> Unit,
    onDescriptionType: (String) -> Unit,
    onTopicNameType: (String) -> Unit,
    onTopicSelect: (DropdownMenuItem) -> Unit,
    onAttachmentClick: (item: AttachmentItem) -> Unit,
    onAttachmentRemove: (position: Int) -> Unit,
    onDueDateTimeSelect: (LocalDate, LocalTime) -> Unit,
    onDueDateTimeClear: () -> Unit
) {
    stateResource.onSuccess { state ->

        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }

        Column(
            Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(horizontal = MaterialTheme.spacing.normal)) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { onNameType(it) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = state.nameMessage != null,
                    placeholder = { Text("Название") },
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
                expanded = expanded,
                onExpandedChange = { expanded = it },
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.normal)) {
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
                headlineContent = { Text("Добавить вложение") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Attachment,
                        contentDescription = "add attachment"
                    )
                }
            )

            attachmentsResource.onSuccess { attachmentItems ->
                if (attachmentItems.isNotEmpty()) {
                    HeaderItemUI(name = "Вложения")
                    LazyRow {
                        itemsIndexed(
                            items = attachmentItems,
                            key = { _, item -> item.attachmentId ?: Unit }) { index, item ->
                            AttachmentItemUI(
                                item = item,
                                onClick = {
                                    onAttachmentClick(item)
                                },
                                onRemove = { onAttachmentRemove(index) })
                        }
                    }
                }
            }

            ListItem(
                modifier = Modifier.clickable {
                    showDatePicker = true
                },
                headlineContent = {
                    Text(
                        text = buildString {
                            state.dueDate?.let { date ->
                                append(Dates.toStringDayMonthHidingCurrentYear(date))
                                state.dueTime?.let { time ->
                                    append(", ${time.toString("HH:mm")}")
                                }
                            } ?: append("Добавить срок сдачи")
                        },
                        modifier = Modifier.alpha(state.dueDate?.let { 1f }
                            ?: ContentAlpha.disabled))
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = "due date and time"
                    )
                },
                trailingContent = state.dueDate?.let {
                    {
                        IconButton(onClick = { onDueDateTimeClear() }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "remove due date time"
                            )
                        }
                    }
                }
            )
        }

        var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

        var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

        if (showDatePicker)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    showDatePicker = false
                    showTimePicker = true
                }) {
                val datePickerState = rememberDatePickerState()
                DatePicker(state = datePickerState)
                datePickerState.selectedDateMillis?.let {
                    selectedDate =
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                }
            }

        if (showTimePicker)
            DatePickerDialog(onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    showTimePicker = false
                    selectedDate?.let { date ->
                        selectedTime?.let { time ->
                            onDueDateTimeSelect(date, time)
                        }
                    }

                }) {
                val timePickerState = rememberTimePickerState(is24Hour = true)
                TimePicker(state = timePickerState)

                timePickerState.apply {
                    selectedTime = LocalTime.of(hour, minute, 0)
                }
            }
    }
}

@Preview
@Composable
fun CourseWorkEditorPreview() {
    AppTheme {
        Surface {
            CourseWorkEditorContent(
                stateResource = Resource.Success(CourseWorkEditorComponent.EditingWork().apply {
                    name = "Контрольная работа"
                    dueDate = LocalDate.now()
                    dueTime = LocalTime.now()
                }),
                attachmentsResource = Resource.Success(emptyList()),
                onNameType = {},
                onDescriptionType = {},
                onTopicNameType = {},
                onTopicSelect = {},
                onAttachmentClick = {},
                onAttachmentRemove = {},
                onDueDateTimeSelect = { _, _ -> },
                onDueDateTimeClear = {}
            )
        }
    }
}