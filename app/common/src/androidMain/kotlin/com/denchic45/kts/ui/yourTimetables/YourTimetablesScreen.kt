package com.denchic45.kts.ui.yourTimetables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.kts.ui.timetable.DayTimetableContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourTimetablesScreen(component: YourTimetablesComponent, appBarInteractor: AppBarInteractor) {
    val selectedTimetable by component.selectedTimetable.collectAsState()
    val groups by component.studyGroups.collectAsState()

    Column {
        groups.onSuccess { groups ->
            var expanded by remember { mutableStateOf(false) }
            val showList = expanded && groups.isNotEmpty()

            ExposedDropdownMenuBox(expanded = showList, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = if (selectedTimetable == -1) "Мое расписание" else groups[selectedTimetable].name,
                    readOnly = true,
                    onValueChange = {},
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = showList
                        )
                    },
                    singleLine = true,
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.small)
                        .menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = showList,
                    onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Мое расписание") },
                        onClick = {
                            component.onTimetableSelect(-1)
                            expanded = false
                        }
                    )
                    groups.forEachIndexed { index, group ->
                        DropdownMenuItem(
                            text = { Text(group.name) },
                            onClick = {
                                component.onTimetableSelect(index)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
//        val timetableComponent = component.timetableComponent
        val selectedDate by component.selectedDate.collectAsState()
        val viewState by component.dayViewState.collectAsState()
        DayTimetableContent(
            selectedDate = selectedDate,
            viewState = viewState,
            onDateSelect = component::onDateSelect
        )
    }
}

@Composable
private fun TimetableItem(title: String, onClick: () -> Unit) {
    Text(text = title, modifier = Modifier
        .clickable { onClick() }
        .padding(8.dp))
}