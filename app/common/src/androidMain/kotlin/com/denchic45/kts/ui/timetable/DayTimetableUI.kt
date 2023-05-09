package com.denchic45.kts.ui.timetable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onLoading
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.timetable.state.DayTimetableViewState
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import java.time.LocalDate

@Composable
fun DayTimetableContent(
    selectedDate: LocalDate,
    viewStateResource: Resource<DayTimetableViewState?>,
    onDateSelect: (date: LocalDate) -> Unit,
    onPeriodAdd: (() -> Unit)? = null,
    onEditClick: ((Int) -> Unit)? = null,
) {
    Surface {
        Column(Modifier.fillMaxSize()) {
            val state = rememberWeekCalendarState(firstVisibleWeekDate = selectedDate)
            WeekCalendar(
                state = state,
                dayContent = { day ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable(
                                onClick = { onDateSelect(day.date) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = day.date.dayOfMonth.toString())
                    }
                }
            )
//            AndroidView(factory = {
//                WeekCalendarView(it).apply {
//                    weekCalendarListener = object : WeekCalendarListener {
//                        override fun onDaySelect(date: LocalDate) {
//                            onDateSelect(date)
//                        }
//
//                        override fun onWeekSelect(weekItem: WeekItem) {}
//                    }
//                }
//            }, update = {
//                if (it.selectDate != selectedDate) {
//                    println(selectedDate)
//                    it.selectDate = selectedDate
//                }
//            },
//                onReset = {},
//                onRelease = {
//                    it.removeListeners()
//                })
            viewStateResource.onSuccess { viewState ->
                viewState?.let {
                    LazyColumn {
                        itemsIndexed(viewState.periods) { index, item ->
                            PeriodItemUI(
                                order = index + 1,
                                item = item,
                                time = viewState.orders[index].time,
                                isEdit = viewState.isEdit,
                                onEditClick = { onEditClick?.invoke(index) }
                            )
                        }
                        if (viewState.isEdit) {
                            item {
                                ListItem(
                                    modifier = Modifier.clickable { onPeriodAdd?.invoke() },
                                    headlineContent = { Text("Добавить") },
                                    leadingContent = {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "add period"
                                        )
                                    }
                                )
                            }
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
}

@Preview
@Composable
fun TimetableEditorContentPreview() {
    DayTimetableContent(
        selectedDate = LocalDate.now(),
        viewStateResource = Resource.Success(
            DayTimetableViewState(
                LocalDate.now(),
                listOf(),
                listOf(),
                6,
                true
            )
        ), {}) {}
}