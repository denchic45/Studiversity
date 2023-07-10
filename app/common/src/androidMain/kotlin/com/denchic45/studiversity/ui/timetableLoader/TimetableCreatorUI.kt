package com.denchic45.studiversity.ui.timetableLoader

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.ViewWeek
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.util.getFile
import com.denchic45.stuiversity.util.DateTimePatterns
import com.denchic45.stuiversity.util.toString
import com.denchic45.stuiversity.util.toToLocalDateTime
import com.denchic45.stuiversity.util.withDayOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableCreatorDialog(component: TimetableCreatorComponent, onDismissRequest:()->Unit) {
    val context = LocalContext.current
    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { result ->
        component.onFileSelect(result?.getFile(context))
    }

    val screenState by component.uiState.collectAsState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Отмена")
            }
        },
        title = { Text(text = "Создать расписание") },
        text = {
            Column {
                ListItem(
                    headlineContent = { Text(text = "С нуля") },
                    leadingContent = {
                        Icon(
                            Icons.Outlined.ViewWeek,
                            contentDescription = "create empty"
                        )
                    },
                    modifier = Modifier.clickable(onClick = component::onCreateEmpty)
                )
                ListItem(
                    headlineContent = { Text(text = "Извлечь из документа") },
                    leadingContent = {
                        Icon(
                            Icons.Outlined.FileOpen,
                            contentDescription = "parse doc"
                        )
                    },
                    modifier = Modifier.clickable(onClick = component::onParseDoc)
                )
            }
        }
    )

    var selectedDate by remember {
        mutableStateOf(LocalDate.now().withDayOfWeek(DayOfWeek.MONDAY))
    }

    val showWeekPicker by component.showWeekPicker.collectAsState()
    val datePickerState = rememberDatePickerState()
    if (showWeekPicker)
        DatePickerDialog(
            onDismissRequest = component::onCancelWeekPicker,
            dismissButton = {
                TextButton(onClick = component::onCancelWeekPicker) {
                    Text(text = "Отмена")
                }
            },
            confirmButton = {
                TextButton(enabled = datePickerState.selectedDateMillis != null, onClick = {
                    component.onWeekSelect(
                        selectedDate.toString(DateTimePatterns.YYYY_ww)
                    )
                }) {
                    Text(text = "ОК")
                }

            }) {
            DatePicker(state = datePickerState,
                dateValidator = {
                    it.toToLocalDateTime().dayOfWeek == WeekFields.of(Locale.getDefault()).firstDayOfWeek
                }
            )
            datePickerState.selectedDateMillis?.let {
                selectedDate = it.toToLocalDateTime().toLocalDate()
            }
        }

    val showFilePicker by component.showFilePicker.collectAsState()
    if (showFilePicker) {
        pickFileLauncher.launch(
            arrayOf(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/msword"
            )
        )
    }

    when (val state = screenState) {
        is TimetableCreatorComponent.UiState.Error -> {
            AlertDialog(onDismissRequest = component::onErrorClose,
                title = { Text(text = "Произошла ошибка") },
                text = { Text(state.message) },
                confirmButton = {
                    Button(onClick = component::onErrorClose) { Text("ОК") }
                }
            )
        }

        is TimetableCreatorComponent.UiState.Loading -> {
            AlertDialog(onDismissRequest = {}) {
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
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.normal))
                        Text(state.message)
                    }
                }
            }
        }

        TimetableCreatorComponent.UiState.None -> {}
    }
}