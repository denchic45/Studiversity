package com.denchic45.kts.ui.timetableEditor

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.denchic45.kts.data.domain.ClientError
import com.denchic45.kts.domain.onFailure
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.timetable.DayTimetableComponent
import com.denchic45.kts.ui.timetable.state.DayTimetableViewState
import com.denchic45.kts.ui.widget.calendar.WeekCalendarListener
import com.denchic45.kts.ui.widget.calendar.WeekCalendarView
import com.denchic45.kts.ui.widget.calendar.model.WeekItem
import java.time.LocalDate

@Composable
fun DayTimetableEditorScreen(component: DayTimetableEditorComponent) {
    val viewState by component.viewState.collectAsState()
    viewState?.let { DayTimetableContent(it, {}, component::onPeriodEdit) }
}

@Composable
fun DayTimetableScreen(component: DayTimetableComponent) {
    val viewState by component.viewState.collectAsState()
    viewState.onSuccess { DayTimetableContent(it, component::onDateSelect) {} }
        .onFailure {
        Log.d("lol", "DayTimetableScreen: ${(it as ClientError).response.error}")
    }
}

@Composable
fun DayTimetableContent(
    viewState: DayTimetableViewState,
    onDateSelect: (date: LocalDate) -> Unit,
    onEditClick: (Int) -> Unit,
) {
    Column {
        AndroidView(factory = {
            WeekCalendarView(it).apply {
                selectDate = viewState.date
                weekCalendarListener = object : WeekCalendarListener {
                    override fun onDaySelect(date: LocalDate) {
                        onDateSelect(date)
                    }

                    override fun onWeekSelect(weekItem: WeekItem) {}
                }
            }
        })
        LazyColumn {
            itemsIndexed(viewState.periods) { index, item ->
                PeriodItemUI(
                    item = item,
                    time = viewState.orders[index].time,
                    isEdit = viewState.isEdit,
                    onEditClick = { onEditClick(index) }
                )
            }
        }
    }
}

@Preview
@Composable
fun TimetableEditorContentPreview() {
    DayTimetableContent(
        viewState = DayTimetableViewState(
            LocalDate.now(),
            listOf(),
            listOf(),
            6,
            true
        ), {}) {}
}