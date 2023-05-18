package com.denchic45.kts.ui.yourtimetables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.LocalAppBarMediator
import com.denchic45.kts.ui.components.ExposedDropdownMenuDefaults
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.kts.ui.timetable.TimetableContent


@Composable
fun YourTimetablesScreen(component: YourTimetablesComponent) {
    val selectedTimetable by component.selectedTimetablePosition.collectAsState()
    val groups by component.studyGroups.collectAsState()

    val appBarMediator = LocalAppBarMediator.current
    appBarMediator.title = "Расписание"
    appBarMediator.content = {
        groups.onSuccess { groups ->
            var expanded by remember { mutableStateOf(false) }
            val showList = expanded && groups.isNotEmpty()

            selectedTimetable.onSuccess { selectedTimetable ->

                OutlinedButton(onClick = { expanded = !expanded }) {
                    Text(
                        text = if (selectedTimetable == -1) "Мое расписание" else groups[selectedTimetable].name,
                        modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                    )

                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                }

                DropdownMenu(
                    expanded = expanded,
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
    }


        Column {
            val timetable by component.timetableState.collectAsState()
            val mondayDate by component.mondayDate.collectAsState()


                TimetableContent(
                    selectedDate = mondayDate,
                    timetableResource = timetable,
                    onTodayClick = component::onTodayClick,
                    onPreviousWeekClick = component::onPreviousWeekClick,
                    onNextWeekClick = component::onNextWeekClick
                )
        }
}