package com.denchic45.studiversity.ui.timetableloader

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.domain.resourceOf
import com.denchic45.studiversity.ui.appbar2.AppBarContent
import com.denchic45.studiversity.ui.appbar2.hideAppBar
import com.denchic45.studiversity.ui.appbar2.updateAppBarState
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.periodeditor.PeriodEditorScreen
import com.denchic45.studiversity.ui.search.StudyGroupChooserScreen
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.timetable.DayTimetableContent
import com.denchic45.studiversity.ui.timetableeditor.TimetableEditorComponent
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimetablesPublisherScreen(
    component: TimetablesPublisherComponent
) {
    updateAppBarState(AppBarContent())

    val publishState by component.publishState.collectAsState()
    val timetablesEditors by component.editorComponents.collectAsState()
    val studyGroups by component.studyGroups.collectAsState()
    val isEdit by component.isEdit.collectAsState()
    val selectedDate by component.selectedDate.collectAsState()
    val overlay by component.childOverlay.subscribeAsState()

    val pagerState = rememberPagerState()

    LaunchedEffect(Unit) {
        component.selectedGroup.collect { index ->
            println("pager state: $index")
            pagerState.animateScrollToPage(index)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            component.onStudyGroupSelect(page)
        }
    }

    TimetablePublisherContent(
        publishState = publishState,
        isEdit = isEdit,
        studyGroups = studyGroups,
        pagerState = pagerState,
        editors = timetablesEditors,
        selectedDate = selectedDate,
        onDateSelect = component::onDateSelect,
        onEditChangeClick = component::onEditEnableClick,
        onPublishClick = component::onPublishClick,
        onStudyGroupChoose = component::onStudyGroupChoose,
        onStudyGroupSelect = component::onStudyGroupSelect,
        onRemoveStudyGroupClick = component::onRemoveStudyGroupClick,
        onAddPeriodClick = component::onAddPeriodClick,
        onEditPeriodClick = component::onEditPeriodClick,
        onRemovePeriodSwipe = component::onRemovePeriodSwipe
    )

    overlay.overlay?.let {
        when (val child = it.instance) {
            is TimetablesPublisherComponent.OverlayChild.StudyGroupChooser -> {
                hideAppBar()
                StudyGroupChooserScreen(
                    component = child.component
                )
            }

            is TimetablesPublisherComponent.OverlayChild.PeriodEditor -> PeriodEditorScreen(
                component = child.component
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun TimetablePublisherContent(
    publishState: TimetablesPublisherComponent.PublishState,
    isEdit: Boolean,
    studyGroups: List<StudyGroupResponse>,
    pagerState: PagerState,
    editors: List<TimetableEditorComponent>,
    selectedDate: LocalDate,
    onDateSelect: (LocalDate) -> Unit,
    onEditChangeClick: (Boolean) -> Unit,
    onPublishClick: () -> Unit,
    onStudyGroupChoose: () -> Unit,
    onStudyGroupSelect: (Int) -> Unit,
    onRemoveStudyGroupClick: (Int) -> Unit,
    onAddPeriodClick: (DayOfWeek) -> Unit,
    onEditPeriodClick: (DayOfWeek, Int) -> Unit,
    onRemovePeriodSwipe: (DayOfWeek, Int) -> Unit,
) {
    Surface {
        Column {
            if (studyGroups.isNotEmpty()) {
                ListItem(
                    headlineContent = {
                        Text(
                            when (publishState) {
                                TimetablesPublisherComponent.PublishState.PREPARATION -> "Опубликовать"
                                TimetablesPublisherComponent.PublishState.SENDING -> "Публикация"
                                TimetablesPublisherComponent.PublishState.DONE -> "Опубликовано"
                                TimetablesPublisherComponent.PublishState.FAILED -> "Неудалось опубликовать"
                            }
                        )
                    },
                    trailingContent = {
                        when (publishState) {
                            TimetablesPublisherComponent.PublishState.PREPARATION -> {
                                Icon(
                                    imageVector = Icons.Outlined.Send,
                                    contentDescription = "publish"
                                )
                            }

                            TimetablesPublisherComponent.PublishState.SENDING -> {
                                Box(modifier = Modifier.size(24.dp)) {
                                    CircularProgressIndicator()
                                }
                            }

                            TimetablesPublisherComponent.PublishState.DONE -> {
                                Icon(
                                    imageVector = Icons.Outlined.Done,
                                    contentDescription = "done"
                                )
                            }

                            TimetablesPublisherComponent.PublishState.FAILED -> {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = "retry"
                                )
                            }
                        }
                    },
                    modifier = Modifier.clickable(
                        enabled = publishState == TimetablesPublisherComponent.PublishState.PREPARATION,
                        onClick = onPublishClick
                    )
                )

                if (publishState == TimetablesPublisherComponent.PublishState.PREPARATION)
                    ListItem(
                        headlineContent = { Text("Режим редактирования") },
                        trailingContent = {
                            Switch(
                                checked = isEdit,
                                onCheckedChange = onEditChangeClick
                            )
                        },
                        modifier = Modifier.clickable { onEditChangeClick(!isEdit) }
                    )
            }

            if (studyGroups.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(MaterialTheme.spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Нет расписаний", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                    Text(
                        text = "Не выбрано ни одной группы для загрузки расписания",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
                    Button(onClick = onStudyGroupChoose) {
                        Text(text = "Добавить группу")
                    }
                }
            } else {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { positions ->
                        TabIndicator(Modifier.tabIndicatorOffset(positions[pagerState.currentPage]))
                    },
                    modifier = Modifier,
                    divider = {}
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    // Add tabs for all of our pages
                    studyGroups.forEachIndexed { index, group ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(group.name)
                                    IconButton(onClick = { onRemoveStudyGroupClick(index) }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "remove group"
                                        )
                                    }
                                }
                            },
                            onClick = { onStudyGroupSelect(index) },
                        )
                    }
                    Tab(
                        selected = false,
                        text = { Text("Добавить") },
                        onClick = onStudyGroupChoose
                    )
                }
                Divider()
                HorizontalPager(
                    pageCount = editors.size,
                    state = pagerState
                ) { position ->
                    val state by editors[position].editingTimetableState.collectAsState()
                    DayTimetableContent(
                        selectedDate = selectedDate,
                        timetableResource = resourceOf(state),
                        isEdit = isEdit,
                        onDateSelect = onDateSelect,
                        onAddPeriodClick = onAddPeriodClick,
                        onEditPeriodClick = onEditPeriodClick,
                        onRemovePeriodSwipe = onRemovePeriodSwipe,
                        scrollableWeeks = false
                    )
                }
            }
        }
    }
}