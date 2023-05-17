package com.denchic45.kts.ui.yourtimetables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.LocalAppBarMediator
import com.denchic45.kts.ui.components.ExposedDropdownMenuBox
import com.denchic45.kts.ui.components.ExposedDropdownMenuDefaults
import com.denchic45.kts.ui.components.Spinner2
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.kts.ui.timetable.TimetableContent2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourTimetablesScreen(component: YourTimetablesComponent) {
    val selectedTimetable by component.selectedTimetablePosition.collectAsState()
    val groups by component.studyGroups.collectAsState()

    val appBarMediator = LocalAppBarMediator.current
    appBarMediator.title = "Расписание"
//    appBarMediator.content = {
//        groups.onSuccess { groups ->
//            var expanded by remember { mutableStateOf(false) }
//            val showList = expanded && groups.isNotEmpty()
//
//            selectedTimetable.onSuccess { selectedTimetable ->
//                Spinner2(
//                    text = if (selectedTimetable == -1) "Мое расписание" else groups[selectedTimetable].name,
//                    expanded = expanded,
//                    onExpandedChange = { expanded = it },
//                    modifier = Modifier.height(48.dp)
//                ) {
//                    DropdownMenuItem(
//                        text = { Text("Мое расписание") },
//                        onClick = {
//                            component.onTimetableSelect(-1)
//                            expanded = false
//                        }
//                    )
//                    groups.forEachIndexed { index, group ->
//                        DropdownMenuItem(
//                            text = { Text(group.name) },
//                            onClick = {
//                                component.onTimetableSelect(index)
//                                expanded = false
//                            }
//                        )
//                    }
//                }
//
//            }
//        }
//    }
//    appBarMediator.apply {
//        LaunchedEffect(selectedYearWeek) {
//            title = getMonthTitle(selectedYearWeek)
//        }
//        content = {
//            val contentHeight = 40.dp
////            Spacer(Modifier.width(24.dp))
//            Spacer(Modifier.weight(1f))
//
//
//
////            Spacer(Modifier.weight(1f))
////            Spinner() TODO add later
////            Spacer(Modifier.width(24.dp))
//        }
//    }

//    LaunchedEffect(selectedYearWeek) {
//        appBarInteractor.set(AppBarState(uiTextOf(getMonthTitle(selectedYearWeek))))
//    }

    Surface {
        Column {
            val timetable by component.timetableState.collectAsState()
            val mondayDate by component.mondayDate.collectAsState()

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxSize().padding(end = 24.dp, bottom = 24.dp),
                elevation = 0.dp
            ) {
                TimetableContent2(
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