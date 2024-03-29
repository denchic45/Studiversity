package com.denchic45.studiversity.ui.yourtimetables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.BlockContent
import com.denchic45.studiversity.ui.ScreenScaffold
import com.denchic45.studiversity.ui.main.AppBarTitle
import com.denchic45.studiversity.ui.main.CustomAppBar
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.timetable.TimetableContent


@Composable
fun YourTimetablesScreen(component: YourTimetablesComponent) {
    val selectedTimetable by component.selectedTimetablePosition.collectAsState()
    val groups by component.studyGroups.collectAsState()

    Column {
        val timetable by component.timetableState.collectAsState()
        val mondayDate by component.mondayDate.collectAsState()

        ScreenScaffold(
            topBar = {
                CustomAppBar(
                    title = {
                        AppBarTitle("Расписание")
                        Spacer(Modifier.width(MaterialTheme.spacing.normal))
                        TimetablesSpinner(groups, selectedTimetable, component)
                    },
                )
            }) {
            BlockContent {
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
}

@OptIn(ExperimentalMaterial3Api::class)
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
    }
}