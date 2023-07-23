package com.denchic45.studiversity.ui.yourtimetables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.ui.AppBarTitle
import com.denchic45.studiversity.ui.CustomAppBar
import com.denchic45.studiversity.ui.Scaffold
import com.denchic45.studiversity.ui.components.ExposedDropdownMenuDefaults
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.timetable.TimetableContent
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse


@Composable
fun YourTimetablesScreen(component: YourTimetablesComponent) {
    val selectedTimetable by component.selectedTimetablePosition.collectAsState()
    val groups by component.studyGroups.collectAsState()

    Column {
        val timetable by component.timetableState.collectAsState()
        val mondayDate by component.mondayDate.collectAsState()

        Scaffold(
            topBar = {
                CustomAppBar(
                    title = {
                            AppBarTitle("Расписание")
                            Spacer(Modifier.width(MaterialTheme.spacing.normal))
                            TimetablesSpinner(groups, selectedTimetable, component)
                    },
                )
            }) {
            TimetableContent(
                selectedDate = mondayDate,
                timetableResource = timetable,
                onTodayClick = component::onTodayClick,
                onPreviousWeekClick = component::onPreviousWeekClick,
                onNextWeekClick = component::onNextWeekClick
            )
        }
    }
}

@Composable
private fun TimetablesSpinner(
    resource: Resource<List<StudyGroupResponse>>,
    selectedTimetable: Resource<Int>,
    component: YourTimetablesComponent
) {
    resource.onSuccess { groups ->
        var expanded by remember { mutableStateOf(false) }
        val showList = expanded && groups.isNotEmpty()

        selectedTimetable.onSuccess { selectedTimetable ->
            Column {
                OutlinedButton(onClick = { expanded = true }) {
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
}