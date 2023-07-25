package com.denchic45.studiversity.ui.yourtimetables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.common.R
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.appbar.ActionMenuItem2
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.updateAppBarState
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.timetable.DayTimetableContent
import com.denchic45.studiversity.ui.timetable.getMonthTitle
import com.denchic45.studiversity.ui.uiIconOf
import com.denchic45.studiversity.ui.uiTextOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourTimetablesScreen(component: YourTimetablesComponent) {
    val selectedTimetable by component.selectedTimetablePosition.collectAsState()
    val groups by component.studyGroups.collectAsState()
    val selectedYearWeek by component.selectedWeekOfYear.collectAsState()
    val selectedDate by component.selectedDate.collectAsState()

    updateAppBarState(
        selectedYearWeek, AppBarContent(
            title = uiTextOf(getMonthTitle(selectedYearWeek)),
            actionItems = listOf(
                ActionMenuItem2(
                    uiIconOf(R.drawable.ic_calendar),
                    onClick = component::onTodayClick
                )
            )
        )
    )

    Surface {
        Column {
            groups.onSuccess { groups ->
                var expanded by remember { mutableStateOf(false) }
                val showList = expanded && groups.isNotEmpty()

                selectedTimetable.onSuccess { selectedTimetable ->
                    ExposedDropdownMenuBox(
                        expanded = showList,
                        onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value = if (selectedTimetable == -1) "Мое расписание" else "Группа " + groups[selectedTimetable].name,
                            readOnly = true,
                            onValueChange = {},
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(showList)
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
                                    text = { Text("Группа " + group.name) },
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

            val viewState by component.timetableState.collectAsState()
            val refreshing by component.refreshing.collectAsState()

            DayTimetableContent(
                selectedDate = selectedDate,
                timetableResource = viewState,
                isEdit = false,
                onDateSelect = component::onDateSelect,
                onStudyGroupClick = component::onStudyGroupClick,
                refreshing = refreshing
            ) { component.refreshing.value = true }
        }
    }
}