package com.denchic45.kts.ui.timetableEditor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onLoading
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
    val selectedDate by component.selectedDate.collectAsState()
    DayTimetableContent(
        selectedDate = selectedDate,
        viewState = viewState,
        component::onDateSelect,
        component::onPeriodEdit
    )
}

@Composable
fun DayTimetableScreen(component: DayTimetableComponent) {
    val viewState by component.viewState.collectAsState()
    val selectedDate by component.selectedDate.collectAsState()
    DayTimetableContent(
        selectedDate = selectedDate,
        viewState = viewState,
        onDateSelect = component::onDateSelect,
        onEditClick = {})
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DayTimetableContent(
    selectedDate: LocalDate,
    viewState: Resource<DayTimetableViewState?>,
    onDateSelect: (date: LocalDate) -> Unit,
    onEditClick: (Int) -> Unit,
) {
    Column {
        AndroidView(factory = {
            WeekCalendarView(it).apply {
                weekCalendarListener = object : WeekCalendarListener {
                    override fun onDaySelect(date: LocalDate) {
                        onDateSelect(date)
                    }
                    override fun onWeekSelect(weekItem: WeekItem) {}
                }
            }
        }, update = {
            if (it.selectDate != selectedDate)
                it.selectDate = selectedDate
        },
            onReset = {
                it.removeListeners()
            },
            onRelease = {
                it.removeListeners()
            })
        viewState.onSuccess { viewState ->
            viewState?.let {
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
        }.onLoading {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview
@Composable
fun TimetableEditorContentPreview() {
    DayTimetableContent(
        selectedDate = LocalDate.now(),
        viewState = Resource.Success(
            DayTimetableViewState(
                LocalDate.now(),
                listOf(),
                listOf(),
                6,
                true
            )
        ), {}) {}
}