package com.denchic45.kts.ui.timetableEditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.denchic45.kts.ui.timetable.DayTimetableViewState
import com.denchic45.kts.ui.widget.calendar.WeekCalendarView
import java.time.LocalDate

@Composable
fun DayTimetableEditorScreen(component: DayTimetableEditorComponent) {
    val viewState by component.viewState.collectAsState()
    viewState?.let { DayTimetableEditorContent(it) }
}

@Composable
fun DayTimetableEditorContent(viewState: DayTimetableViewState) {
    Column {
        AndroidView(factory = {
            WeekCalendarView(it).apply {
                selectDate = viewState.date
            }
        })
        LazyColumn {
            itemsIndexed(viewState.periods) { index, item ->
                PeriodItemUI(item = item, time = viewState.orders[index].time)
            }
        }
    }
}

@Preview
@Composable
fun TimetableEditorContentPreview() {
    DayTimetableEditorContent(
        viewState = DayTimetableViewState(
            LocalDate.now(),
            listOf(),
            listOf(),
            6
        )
    )
}