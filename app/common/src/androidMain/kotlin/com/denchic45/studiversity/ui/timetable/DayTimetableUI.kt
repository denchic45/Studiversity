package com.denchic45.studiversity.ui.timetable

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.denchic45.studiversity.common.R
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.IconTitleBox
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.timetable.state.TimetableState
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DayTimetableContent(
    selectedDate: LocalDate,
    timetableResource: Resource<TimetableState>,
    isEdit: Boolean,
    onDateSelect: (monday: LocalDate) -> Unit,
    onAddPeriodClick: ((DayOfWeek) -> Unit)? = null,
    onEditPeriodClick: ((DayOfWeek, Int) -> Unit)? = null,
    onMovePeriodDrag: ((DayOfWeek, Int, Int) -> Unit)? = null,
    onRemovePeriodSwipe: ((DayOfWeek, Int) -> Unit)? = null,
    onStudyGroupClick: ((studyGroupId: UUID) -> Unit)? = null,
    startDate: LocalDate = selectedDate.minusWeeks(1),
    endDate: LocalDate = selectedDate.plusMonths(1),
    scrollableWeeks: Boolean = true,
    refreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null
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
            LaunchedEffect(selectedDate) {
                state.animateScrollToWeek(selectedDate)
            }
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
                                .clickable(onClick = { onDateSelect(day.date) }),
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

            val timetableContent: @Composable () -> Unit = {
                ResourceContent(resource = timetableResource) { timetableState ->
                    val selectedDayOfWeek = selectedDate.dayOfWeek
                    if (timetableState.contains(selectedDate)) {
                        Periods(
                            timetableState = timetableState,
                            isEdit = isEdit,
                            selectedDayOfWeek = selectedDayOfWeek,
                            onAddPeriodClick = onAddPeriodClick,
                            onEditPeriodClick = onEditPeriodClick,
                            onMovePeriodDrag = onMovePeriodDrag,
                            onRemovePeriodSwipe = onRemovePeriodSwipe,
                            onStudyGroupClClick = onStudyGroupClick
                        )
                    }
                }
            }

            onRefresh?.let {
                val refreshState = rememberPullRefreshState(refreshing, onRefresh)
                Box(
                    modifier = Modifier
                        .pullRefresh(refreshState)
                ) {
                    timetableContent()
                    if (refreshState.progress != 0f)
                        PullRefreshIndicator(
                            refreshing,
                            refreshState,
                            Modifier.align(Alignment.TopCenter)
                        )
                }
            } ?: timetableContent()
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
private fun Periods(
    timetableState: TimetableState,
    isEdit: Boolean,
    selectedDayOfWeek: DayOfWeek,
    onAddPeriodClick: ((DayOfWeek) -> Unit)?,
    onEditPeriodClick: ((DayOfWeek, Int) -> Unit)?,
    onMovePeriodDrag: ((DayOfWeek, Int, Int) -> Unit)?,
    onRemovePeriodSwipe: ((DayOfWeek, Int) -> Unit)?,
    onStudyGroupClClick: ((UUID) -> Unit)?,
) {
    val scrollState = rememberScrollState()
    Crossfade(targetState = selectedDayOfWeek) { dayOfWeek ->
        if (dayOfWeek.value == 7) {
            IconTitleBox(
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
            val items = timetableState.getByDay(dayOfWeek)
            if (items.isEmpty() && !isEdit) {
                IconTitleBox(
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
                val reorderableState = rememberReorderableLazyListState(
                    onMove = { from, to ->
                        onMovePeriodDrag?.invoke(dayOfWeek, from.index, to.index)
                    },
                    canDragOver = { from, to ->
                        val canDragOver = from.index < items.size
                        println("ON MOVE FROM: ${from.index} TO: ${to.index} SIZE: ${items.size} BOOL: $canDragOver")
                        canDragOver
                    },
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .reorderable(reorderableState),
                    state = reorderableState.listState,
                ) {
                    itemsIndexed(
                        items = items,
                        key = { _, item -> item.id }
                    ) { index, item ->
                        val periodListItem = @Composable {
                            PeriodListItem(
                                order = index + 1,
                                item = item,
                                time = timetableState.getOrderTime(index),
                                showStudyGroup = timetableState.showStudyGroups,
                                isEdit = isEdit,
                                onEditClick = {
                                    onEditPeriodClick?.invoke(
                                        selectedDayOfWeek,
                                        index
                                    )
                                },
                                onStudyGroupClick = onStudyGroupClClick
                            )
                        }
                        if (isEdit) {
                            val currentIndex by rememberUpdatedState(index)
                            val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
                                when (it) {
                                    SwipeToDismissBoxValue.Settled -> false
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        onRemovePeriodSwipe?.invoke(selectedDayOfWeek, currentIndex)
                                        true
                                    }

                                    else -> throw IllegalStateException()
                                }
                            })
                            ReorderableItem(reorderableState, key = item.id) { isDragging ->
                                val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)
                                val zIndex = remember(isDragging) {
                                    if (isDragging) 1f else 0f
                                }
                                Box(
                                    modifier = Modifier
                                        .detectReorderAfterLongPress(reorderableState)
                                        .zIndex(zIndex)
                                        .shadow(elevation)
                                ) {
                                    SwipeToDismiss(
                                        state = dismissState,
                                        background = { SwipePeriodBackground(dismissState) },
                                        dismissContent = { periodListItem() },
                                        directions = setOf(SwipeToDismissBoxValue.EndToStart)
                                    )
                                }
                            }
                        } else {
                            periodListItem()
                        }
                    }
                    if (isEdit) {
                        item {
                            ListItem(
                                modifier = Modifier.clickable {
                                    onAddPeriodClick?.invoke(selectedDayOfWeek)
                                },
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
fun SwipePeriodBackground(dismissState: SwipeToDismissBoxState) {
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            SwipeToDismissBoxValue.Settled -> Color.LightGray
            SwipeToDismissBoxValue.EndToStart -> Color.Red
            else -> throw IllegalStateException()
        }
    )
    val alignment = Alignment.CenterEnd
    val icon = Icons.Outlined.Delete
    val scale by animateFloatAsState(
        if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f
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