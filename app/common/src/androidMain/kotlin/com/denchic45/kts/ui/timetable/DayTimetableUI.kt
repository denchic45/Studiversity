package com.denchic45.kts.ui.timetable

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.denchic45.kts.R
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onLoading
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.search.IconTitle
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.kts.ui.timetable.state.TimetableState
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.random.Random

@Composable
fun DayTimetableContent(
    selectedDate: LocalDate,
    timetableResource: Resource<TimetableState>,
    onDateSelect: (date: LocalDate) -> Unit,
    onAddPeriodClick: (() -> Unit)? = null,
    onEditPeriodClick: ((Int) -> Unit)? = null,
    onRemovePeriodSwipe: ((Int) -> Unit)? = null,
    startDate: LocalDate = selectedDate.minusWeeks(1),
    endDate: LocalDate = selectedDate.plusMonths(1),
    scrollableWeeks: Boolean = true,
) {
    Surface {
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = MaterialTheme.spacing.small)
        ) {
            val state = rememberWeekCalendarState(
                startDate = startDate,
                endDate = endDate,
                firstVisibleWeekDate = selectedDate,
                firstDayOfWeek = DayOfWeek.MONDAY
            )
            val daysOfWeek = daysOfWeek(DayOfWeek.MONDAY)
            Row {
                daysOfWeek.forEach {
                    Text(
                        text = it.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            WeekCalendar(
                state = state,
                userScrollEnabled = scrollableWeeks,
                dayContent = { day ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(MaterialTheme.spacing.extraSmall)
                                .clip(CircleShape)
                                .then(
                                    if (selectedDate == day.date)
                                        Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
                                    else Modifier
                                )
                                .clickable(
                                    onClick = { onDateSelect(day.date) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            )

            timetableResource.onSuccess { timetableState ->
                val selectedDayOfWeek = selectedDate.dayOfWeek
                if (timetableState.contains(selectedDate)) {
                    Periods(
                        timetable = timetableState,
                        selectedDayOfWeek = selectedDayOfWeek,
                        onEditPeriodClick = onEditPeriodClick,
                        onRemovePeriodSwipe = onRemovePeriodSwipe,
                        onAddPeriodClick = onAddPeriodClick
                    )
                }

            }.onLoading {
                TimetableLoading()
            }
        }
    }
}

private fun getWeek(selectedDate: LocalDate): Int {
    return selectedDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
}

@Composable
private fun TimetableLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Periods(
    timetable: TimetableState,
    selectedDayOfWeek: DayOfWeek,
    onEditPeriodClick: ((Int) -> Unit)?,
    onRemovePeriodSwipe: ((Int) -> Unit)?,
    onAddPeriodClick: (() -> Unit)?,
) {
    val scrollState = rememberScrollState()
    Crossfade(
        targetState = selectedDayOfWeek,
    ) { dayOfWeek ->
        if (dayOfWeek.value == 7) {
            IconTitle(
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.ill_day_off),
                        contentDescription = "day off"
                    )
                },
                title = { Text(text = "Выходной день") },
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(scrollState)
            )
        } else {
            val items = timetable.getDay(selectedDayOfWeek)
            if (items.isEmpty()) {
                IconTitle(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.ill_empty_lessons),
                            contentDescription = "empty lessons"
                        )
                    },
                    title = { Text(text = "Пусто") },
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(scrollState)
                )
            } else {
                LazyColumn {
                    itemsIndexed(
                        items = items,
                        key = { _, item -> item?.id ?: Random.nextLong(0, 1000) }
                    ) { index, item ->
                        val periodItemUI = @Composable {
                            PeriodListItem(
                                order = timetable.orders[index].order,
                                item = item,
                                time = timetable.orders[index].time,
                                isEdit = timetable.isEdit,
                                onEditClick = { onEditPeriodClick?.invoke(index) }
                            )
                        }
                        if (timetable.isEdit) {
                            val currentIndex by rememberUpdatedState(index)
                            val dismissState = rememberDismissState(confirmValueChange = {
                                when (it) {
                                    DismissValue.Default -> false
                                    DismissValue.DismissedToStart -> {
                                        onRemovePeriodSwipe?.invoke(currentIndex)
                                        true
                                    }

                                    else -> throw IllegalStateException()
                                }
                            })
                            SwipeToDismiss(
                                state = dismissState,
                                background = { SwipePeriodBackground(dismissState) },
                                dismissContent = { periodItemUI() },
                                directions = setOf(DismissDirection.EndToStart)
                            )
                        } else {
                            periodItemUI()
                        }
                    }
                    if (timetable.isEdit) {
                        item {
                            ListItem(
                                modifier = Modifier.clickable { onAddPeriodClick?.invoke() },
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipePeriodBackground(dismissState: DismissState) {
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> Color.LightGray
            DismissValue.DismissedToStart -> Color.Red
            else -> throw IllegalStateException()
        }
    )
    val alignment = Alignment.CenterEnd
    val icon = Icons.Outlined.Delete
    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = MaterialTheme.spacing.medium),
        contentAlignment = alignment
    ) {
        Icon(
            icon,
            contentDescription = "Localized description",
            modifier = Modifier.scale(scale)
        )
    }
}


//@Preview
//@Composable
//fun TimetableEditorContentPreview() {
//    DayTimetableContent(
//        selectedDate = LocalDate.now(),
//        timetableResource = Resource.Success(
//            DayTimetableViewState(
//                LocalDate.now(),
//                listOf(),
//                listOf(),
//                6,
//                true
//            )
//        ),
//        onDateSelect = {})
//}