package com.denchic45.studiversity.ui.timetable

import androidx.compose.animation.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denchic45.studiversity.data.service.model.PeriodTime
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.onLoading
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.timetable.model.PeriodDetails
import com.denchic45.studiversity.domain.timetable.model.PeriodItem
import com.denchic45.studiversity.domain.timetable.model.PeriodSlot
import com.denchic45.studiversity.domain.timetable.model.Window
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.timetable.state.TimetableState
import com.denchic45.studiversity.ui.yourtimetables.YourTimetablesComponent
import com.denchic45.stuiversity.util.DateTimePatterns
import com.denchic45.stuiversity.util.toString
import com.seiko.imageloader.rememberAsyncImagePainter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Preview
@Composable
fun TimetableScreen(component: YourTimetablesComponent) {
    val timetable by component.timetableState.collectAsState()
    val mondayDate by component.selectedDate.collectAsState()

    Column {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxSize().padding(end = 24.dp, bottom = 24.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            TimetableContent(
                selectedDate = mondayDate,
                timetableResource = timetable,
                onTodayClick = component::onTodayClick,
                onPreviousWeekClick = component::onPreviousWeekClick,
                onNextWeekClick = component::onNextWeekClick
            )
        }
    }
}

@Composable
fun TimetableContent(
    selectedDate: LocalDate,
    timetableResource: Resource<TimetableState>,
    onTodayClick: () -> Unit,
    onPreviousWeekClick: () -> Unit,
    onNextWeekClick: () -> Unit,
) {
    val verticalScroll: ScrollState = rememberScrollState()
    val horizontalScroll: ScrollState = rememberScrollState()

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxSize().padding(end = 24.dp, bottom = 24.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        BoxWithConstraints(Modifier) {
            val modifierHorScroll =
                if (maxWidth < 1000.dp)
                    Modifier.horizontalScroll(horizontalScroll).widthIn(1000.dp)
                else Modifier

            Column(Modifier.fillMaxWidth()) {
                TimetableBar(
                    onTodayClick = onTodayClick,
                    onPreviousWeekClick = onPreviousWeekClick,
                    onNextWeekClick = onNextWeekClick,
                    selectedDate = selectedDate
                )

                AnimatedContent(
                    targetState = selectedDate,
                    transitionSpec = {
                        if (initialState > targetState) {
                            (slideInHorizontally { -it / 6 } + fadeIn()) togetherWith slideOutHorizontally { it / 6 } + fadeOut()
                        } else {
                            (slideInHorizontally { it / 6 } + fadeIn()) togetherWith slideOutHorizontally { -it / 6 } + fadeOut()
                        }
                    }
                ) { monday ->
                    Column {
                        Row {
                            Box(
                                Modifier.size(78.dp, 124.dp),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                Divider(Modifier.width(1.dp).height(24.dp))
                            }
                            DaysOfWeekHeader(modifierHorScroll, monday)
                        }
                        AnimatedContent(
                            targetState = timetableResource,
                            transitionSpec = { fadeIn() togetherWith fadeOut() }
                        ) { timetableState ->
                            Divider()
                            Row {
                                timetableState.onSuccess { state ->
                                    LessonOrders(state, verticalScroll)
                                    LessonCells(modifierHorScroll, verticalScroll, state)
                                }.onLoading {
                                    Box(
                                        Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        LinearProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimetableBar(
    onTodayClick: () -> Unit,
    onPreviousWeekClick: () -> Unit,
    onNextWeekClick: () -> Unit,
    selectedDate: LocalDate,
) {
    Row(
        Modifier.fillMaxWidth().height(64.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 78.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(onClick = onTodayClick) {
            Text("Сегодня")
        }
        Spacer(Modifier.width(MaterialTheme.spacing.small))
        IconButton(onClick = onPreviousWeekClick) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "previous week arrow icon"
            )
        }
        IconButton(
            onClick = onNextWeekClick
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "next week arrow icon"
            )
        }
        Spacer(Modifier.width(MaterialTheme.spacing.small))
        Text(
            text = getMonthTitle(
                selectedDate.format(DateTimeFormatter.ofPattern(DateTimePatterns.YYYY_ww))
            ),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun LessonOrders(timetableState: TimetableState, scrollState: ScrollState) {
    Column(
        modifier = Modifier.width(78.dp).verticalScroll(scrollState),
        horizontalAlignment = Alignment.End
    ) {
        repeat(maxOf(timetableState.maxEventsOfWeek, 6)) { position ->
            timetableState.getOrderTime(position)
            LessonsOrder(position + 1, timetableState.getOrderTime(position))
        }
    }
}

@Composable
private fun LessonsOrder(order: Int, time: PeriodTime?) {
    Row(Modifier.height(129.dp)) {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                time?.start ?: "-",
                Modifier.padding(top = 8.dp, end = 16.dp),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                order.toString(),
                Modifier.padding(top = 4.dp, end = 16.dp),
                color = Color.Gray,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.weight(1f))
            Divider(Modifier.width(30.dp))
        }
        Divider(Modifier.fillMaxHeight().width(1.dp))
    }
}


@Composable
private fun DaysOfWeekHeader(modifier: Modifier, mondayDate: LocalDate) {
    Row(modifier) {
        repeat(6) { DayOfWeekCell(mondayDate.plusDays(it.toLong())) }
    }
    Divider(Modifier.fillMaxWidth().height(1.dp))
}

@Composable
@Preview
fun RowScope.DayOfWeekCell(date: LocalDate) {
    Row(Modifier.weight(1f).height(IntrinsicSize.Max), verticalAlignment = Alignment.Bottom) {
        if (date.dayOfWeek.ordinal != 0) Divider(Modifier.width(1.dp).height(24.dp))
        Column(
            Modifier.widthIn(min = 196.dp).height(124.dp).padding(top = 24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                date.toString(DateTimePatterns.E).uppercase(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                date.dayOfMonth.toString(),
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}


@Composable
@Preview
fun LessonCells(
    modifier: Modifier,
    verticalScroll: ScrollState,
    state: TimetableState,
) {
    Row(modifier.verticalScroll(verticalScroll)) {
        repeat(6) { dayOfWeek ->
            val maxRows = maxOf(state.maxEventsOfWeek, 6)
            Row(Modifier.weight(1F).height(IntrinsicSize.Max)) {
                if (dayOfWeek != 0) {
                    Divider(Modifier.width(1.dp).fillMaxHeight())
                }
                Column(Modifier.fillMaxWidth()) {
                    state.dayTimetables[dayOfWeek].forEach {
                        PeriodCell(it)
                        Divider(Modifier.fillMaxWidth().height(1.dp))
                    }
                    repeat(maxRows - state.dayTimetables[dayOfWeek].size) {
                        EmptyCell()
                        Divider(Modifier.fillMaxWidth().height(1.dp))
                    }

//                    repeat(timetable.maxEventsSize) { eventOrder ->
//                        LessonCell(timetable.periods[dayOfWeek][eventOrder])
//                        if (eventOrder != 7) Divider(Modifier.fillMaxWidth().height(1.dp))
//                    }
                }
            }
        }
    }
}

@Composable
fun Cell(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.widthIn(min = 196.dp).height(128.dp).padding(MaterialTheme.spacing.normal)) {
        content()
    }
}

@Composable
@Preview
fun PeriodCell(item: PeriodSlot) {
    Cell {
        when (item) {
            is PeriodItem -> {
                when (val details = item.details) {
                    is PeriodDetails.Lesson -> {

                        details.course.subject?.iconUrl?.let {
                            Icon(
                                painter = rememberAsyncImagePainter(it),
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.secondary,
                                contentDescription = null
                            )
                        }

                        Spacer(Modifier.weight(1f))
                        Text(
                            details.course.subject?.name ?: "null",
                            Modifier.padding(top = MaterialTheme.spacing.extraSmall),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "2-й корпус",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    is PeriodDetails.Event -> Text(
                        "Пусто",
                        fontSize = TextUnit(18F, TextUnitType.Sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            is Window -> {
                Text(
                    "Пусто",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
fun EmptyCell() {
    Cell {}
}