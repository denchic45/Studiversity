package com.denchic45.kts.ui.timetableLoader

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.domain.resourceOf
import com.denchic45.kts.ui.chooser.StudyGroupChooserScreen
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.kts.ui.timetable.DayTimetableContent
import com.denchic45.kts.ui.timetable.state.DayTimetableViewState
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimetablesPublisherScreen(component: TimetablesPublisherComponent) {
    val publishState by component.publishState.collectAsState()
    val viewStates by component.timetablesViewStates.collectAsState()
    val studyGroups by component.studyGroups.collectAsState()
    val isEdit by component.isEdit.collectAsState()
    val selectedDate by component.selectedDate.collectAsState()
    val selectedGroup by component.selectedGroup.collectAsState()
    val overlay by component.childOverlay.subscribeAsState()

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    LaunchedEffect(Unit) {
        component.selectedGroup.collect { index ->
//            coroutineScope.launch {
            pagerState.animateScrollToPage(index)
//            }
        }
    }

    LaunchedEffect(pagerState) {
        // Collect from the pager state a snapshotFlow reading the currentPage
        snapshotFlow(pagerState::currentPage).collect { page ->
            component.onStudyGroupClick(page)
        }
    }

    overlay.overlay?.let {
        when (val child = it.instance) {
            is TimetablesPublisherComponent.OverlayChild.GroupChooser -> {
                StudyGroupChooserScreen(component = child.component)
            }
        }
    } ?: run {
        TimetablePublisherContent(
            publishState = publishState,
            isEdit = isEdit,
            studyGroups = studyGroups,
            pagerState = pagerState,
            viewStates = viewStates,
            selectedDate = selectedDate,
            onDateSelect = component::onDateSelect,
            onEditEnableClick = component::onEditEnableClick,
            onPublishClick = component::onPublishClick,
            onStudyGroupChoose = component::onStudyGroupChoose,
            onStudyGroupClick = component::onStudyGroupClick,
            onPeriodAdd= component::onPeriodAdd,
            onPeriodEdit = component::onPeriodEdit
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun TimetablePublisherContent(
    publishState: TimetablesPublisherComponent.PublishState,
    isEdit: Boolean,
    studyGroups: List<StudyGroupResponse>,
    pagerState: PagerState,
    viewStates: List<StateFlow<DayTimetableViewState>>,
    selectedDate: LocalDate,
    onDateSelect: (LocalDate) -> Unit,
    onEditEnableClick: (Boolean) -> Unit,
    onPublishClick: () -> Unit,
    onStudyGroupChoose: () -> Unit,
    onStudyGroupClick: (Int) -> Unit,
    onPeriodAdd:()->Unit,
    onPeriodEdit: (Int, Int) -> Unit
) {
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
                modifier = Modifier.clickable(onClick = onPublishClick)
            )

            ListItem(
                headlineContent = { Text("Режим редактирования") },
                trailingContent = {
                    Switch(
                        checked = isEdit,
                        onCheckedChange = onEditEnableClick
                    )
                },
                modifier = Modifier.clickable { onEditEnableClick(!isEdit) }
            )
        }

        if (studyGroups.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = { onStudyGroupChoose() }) {
                    Text(text = "Добавить группу")
                }
            }
        } else {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                // Add tabs for all of our pages
                studyGroups.forEachIndexed { index, group ->
                    Tab(
                        text = { Text(group.name) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            onStudyGroupClick(index)
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            HorizontalPager(
                pageCount = viewStates.size,
                state = pagerState
            ) { position ->
                val viewState by viewStates[position].collectAsState()
                DayTimetableContent(selectedDate,
                    resourceOf(viewState),
                    onDateSelect = { onDateSelect(it) },
                    onPeriodAdd = {onPeriodAdd()},
                    onEditClick = { onPeriodEdit(position, it) }
                )
            }
        }
    }
}