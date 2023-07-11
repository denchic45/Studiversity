package com.denchic45.studiversity.ui.studygroup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.ui.studygroup.timetable.StudyGroupTimetableComponent
import com.denchic45.studiversity.ui.timetable.TimetableContent

@Composable
fun StudyGroupTimetableScreen(component: StudyGroupTimetableComponent) {
    val timetable by component.timetableState.collectAsState()
    val selectedDate by component.mondayDate.collectAsState()

    TimetableContent(
        selectedDate = selectedDate,
        timetableResource = timetable,
        onTodayClick = component::onTodayClick,
        onPreviousWeekClick = component::onPreviousWeekClick,
        onNextWeekClick = component::onNextWeekClick
    )
}
