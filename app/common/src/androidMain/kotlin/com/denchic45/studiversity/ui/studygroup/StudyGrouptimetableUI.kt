package com.denchic45.studiversity.ui.studygroup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.ui.studygroup.timetable.StudyGroupTimetableComponent
import com.denchic45.studiversity.ui.timetable.DayTimetableContent

@Composable
fun StudyGroupTimetableScreen(component: StudyGroupTimetableComponent) {
    val timetableState by component.timetableState.collectAsState()
    val monday by component.selectedDate.collectAsState()

    DayTimetableContent(
        selectedDate = monday,
        timetableResource = timetableState,
        onDateSelect = component::onDateSelect,
        isEdit = false
    )
}