package com.denchic45.studiversity.ui.coursetimetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.ui.timetable.DayTimetableContent

@Composable
fun CourseTimetableScreen(component: CourseTimetableComponent) {
    val selectedDate by component.selectedDate.collectAsState()
    val timetableResource by component.timetable.collectAsState()

    DayTimetableContent(
        selectedDate = selectedDate,
        timetableResource = timetableResource,
        onDateSelect = component::onDateSelect
    )
}